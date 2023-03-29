package pers.project.blog.strategy;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 上传策略接口
 *
 * @author Luo Fei
 * @version 2023/03/29
 */
public interface UploadStrategy {

    /**
     * 上传文件
     *
     * @param multipartFile 多部分请求中收到的上传文件
     * @param directoryUri  上传目录 URI
     *                      <P>（以分隔符结尾，不以分隔符开始）
     * @return 文件访问 URL
     */
    String uploadFile(MultipartFile multipartFile, String directoryUri);

    /**
     * 上传文件
     *
     * @param inputStream  文件输入流
     * @param directoryUri 上传目录 URI
     *                     <P>（以分隔符结尾，不以分隔符开始）
     * @param fileName     文件名（带扩展名）
     * @return 文件访问 URL
     */
    String uploadFile(InputStream inputStream, String directoryUri, String fileName);

}
