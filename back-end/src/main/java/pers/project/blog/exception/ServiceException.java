package pers.project.blog.exception;

/**
 * 服务异常
 *
 * @author Luo Fei
 * @date 2023/1/2
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
