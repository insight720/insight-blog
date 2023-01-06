package pers.project.blog.exception;

/**
 * 文件上传异常
 *
 * @author Luo Fei
 * @date 2023/1/3
 */
public class FileUploadException extends RuntimeException {

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }

}
