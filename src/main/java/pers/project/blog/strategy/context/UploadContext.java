package pers.project.blog.strategy.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.property.UploadProperties;
import pers.project.blog.strategy.UploadStrategy;

import java.io.InputStream;

/**
 * 上传策略上下文
 *
 * @author Luo Fei
 * @version 2023/03/29
 */
@Component
@EnableConfigurationProperties(UploadProperties.class)
public final class UploadContext {

    private static UploadStrategy uploadStrategy;

    private UploadContext() {
    }

    @Autowired
    public void setUploadStrategy(UploadStrategy uploadStrategy) {
        UploadContext.uploadStrategy = uploadStrategy;
    }

    /**
     * 执行文件上传策略
     *
     * @param multipartFile 上传文件
     * @param directoryUri  上传目录 URI
     *                      <P>（以分隔符结尾，不以分隔符开始）
     * @return 文件访问 URL
     */
    public static String executeStrategy(MultipartFile multipartFile, String directoryUri) {
        return uploadStrategy.uploadFile(multipartFile, directoryUri);
    }

    /**
     * 执行文件上传策略
     *
     * @param inputStream  文件输入流
     * @param directoryUri 上传目录 URI
     *                     <P>（以分隔符结尾，不以分隔符开始）
     * @param fileName     文件名（带扩展名）
     * @return 文件访问 URL
     */
    public static String executeStrategy(InputStream inputStream, String directoryUri, String fileName) {
        return uploadStrategy.uploadFile(inputStream, directoryUri, fileName);
    }

}
