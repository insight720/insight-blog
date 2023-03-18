package pers.project.blog.exception;

/**
 * 服务异常
 * <p>
 * （用异常实现正常的服务逻辑）
 *
 * @author Luo Fei
 * @version 2023/1/2
 */
public class ServiceException extends RuntimeException {

    /**
     * 只有异常信息的服务异常（不会通知博主异常）
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * 带有异常信息和原因的异常（通知博主异常异常原因）
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
