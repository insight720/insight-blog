package pers.project.blog.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pers.project.blog.dto.Result;
import pers.project.blog.exception.ServiceException;

/**
 * 全局异常处理程序
 *
 * @author Luo Fei
 * @date 2023/1/6
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // TODO: 2023/1/6 细化异常处理，加入参数校验异常

    @ExceptionHandler(value = ServiceException.class)
    public Result<?> serviceException(Exception serviceException) {
        return Result.error(serviceException);
    }

    @ExceptionHandler
    public Result<?> anyException(Exception exception) {
        log.error("未知异常", exception);
        return Result.error(exception);
    }

}
