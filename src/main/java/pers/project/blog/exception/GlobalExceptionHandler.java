package pers.project.blog.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pers.project.blog.dto.bloginfo.WebsiteConfig;
import pers.project.blog.dto.comment.EmailDTO;
import pers.project.blog.util.*;

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
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> serviceException(ServiceException exception) {
        Throwable cause = exception.getCause();
        if (cause != null) {
            // 有原因的服务异常可能包含预期外的错误
            tryNotify(cause);
            log.warn("服务异常原因: ", cause);
        }
        return Result.error(exception);
    }

    // region 参数验证

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> constraintViolationException(ConstraintViolationException exception) {
        // 参数验证异常: methodName.fieldName: message, methodName.fieldName: message
        String message = exception.getMessage();
        log.warn("参数验证异常: {}", message);
        // fieldName: message, fieldName: message
        String content = Arrays.stream(message.split(","))
                .map(s -> s.trim().substring(s.indexOf('.') + 1))
                .collect(Collectors.joining(", "));
        return Result.error(content);
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> bindException(BindException exception) {
        // 参数验证异常: objectName.fieldName: message
        String objectName = exception.getBindingResult().getObjectName();
        String message = exception.getFieldErrors()
                .stream()
                .map(fieldError -> String.format("%s.%s: %s",
                        objectName, fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        log.warn("参数验证异常: {}", message);
        return Result.error(message);
    }

    // endregion

    @ExceptionHandler(value = Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> throwable(Throwable throwable) {
        tryNotify(throwable);
        log.error("未知异常: ", throwable);
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
