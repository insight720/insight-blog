package pers.project.blog.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.Lombok;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pers.project.blog.dto.bloginfo.WebsiteConfig;
import pers.project.blog.dto.comment.EmailDTO;
import pers.project.blog.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static pers.project.blog.constant.GenericConst.ONE;
import static pers.project.blog.constant.GenericConst.TRUE_OF_INT;
import static pers.project.blog.enums.ResultEnum.INTERNAL_SERVER_ERROR;

/**
 * 全局异常处理程序
 *
 * @author Luo Fei
 * @date 2023/1/6
 */
@Slf4j
@RestControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    /**
     * 受检查异常处理示例
     * <p>
     * 此项目将受检查异常包装到 RuntimeException 中重新抛出，
     * 这样会使异常堆栈信息中增加无意义的 Caused by 内容。
     * <p>
     * 推荐改为以下两种处理方式中的一种。
     */
    private static void exampleOfCheckedExceptionHandling() {
        reThrow();
        sneakyThrows();
    }

    /**
     * @see Lombok#sneakyThrow(Throwable)
     * @see ExceptionUtils#rethrow(Throwable)
     */
    private static void reThrow() {
        try {
            throw new Exception();
        } catch (Exception e) {
            throw Lombok.sneakyThrow(new Exception());
            // 也可以使用 ExceptionUtils，更推荐 Lombok
//            ExceptionUtils.rethrow(e);
        }
    }

    /**
     * @see SneakyThrows
     */
    @SneakyThrows
    private static void sneakyThrows() {
        throw new Exception();
    }

    @ExceptionHandler(value = ServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> serviceException(HttpServletRequest request, ServiceException exception) {
        Throwable cause = exception.getCause();
        if (cause != null) {
            // 有原因的服务异常可能包含预期外的错误
            tryNotify(cause);
            log.warn("服务异常原因: ", cause);
            log.warn("服务异常 URI: {}", request.getRequestURI());
        }
        return Result.error(exception);
    }

    // region 参数验证

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> constraintViolationException(HttpServletRequest request,
                                                  ConstraintViolationException exception) {
        // 参数验证异常: methodName.fieldName: message, methodName.fieldName: message
        String message = exception.getMessage();
        log.warn("参数验证异常: {}", message);
        log.warn("参数验证异常 URI: {}", request.getRequestURI());
        // fieldName: message, fieldName: message
        String content = Arrays.stream(message.split(","))
                .map(s -> s.trim().substring(s.indexOf('.') + 1))
                .collect(Collectors.joining(", "));
        return Result.error(content);
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> bindException(HttpServletRequest request, BindException exception) {
        // 参数验证异常: objectName.fieldName: message
        String objectName = exception.getBindingResult().getObjectName();
        String message = exception.getFieldErrors()
                .stream()
                .map(fieldError -> String.format("%s.%s: %s",
                        objectName, fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        log.warn("参数验证异常: {}", message);
        log.warn("参数验证异常 URI: {}", request.getRequestURI());
        return Result.error(message);
    }

    // endregion

    @ExceptionHandler(value = Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> throwable(HttpServletRequest request, Throwable throwable) {
        tryNotify(throwable);
        log.error("未知异常: ", throwable);
        log.error("未知异常 URI: {}", request.getRequestURI());
        return Result.of(INTERNAL_SERVER_ERROR);
    }

    /**
     * 通知异常信息
     */
    public static void tryNotify(Throwable throwable) {
        if (ConfigUtils.getCache(WebsiteConfig::getNotifyError).equals(TRUE_OF_INT)) {
            AsyncUtils.runAsync(() -> {
                EmailDTO emailDTO = EmailDTO.builder()
                        .email(ConfigUtils.getCache(WebsiteConfig::getEmail))
                        .subject(String.format("博客异常: %s", TimeUtils.format(TimeUtils.now())))
                        .content(ExceptionUtil.stacktraceToString(throwable, -ONE))
                        .build();
                RabbitUtils.sendEmail(emailDTO);
            });
        }
    }

}
