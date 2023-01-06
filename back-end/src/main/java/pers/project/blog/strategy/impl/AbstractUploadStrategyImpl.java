package pers.project.blog.strategy.impl;

import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.exception.FileUploadException;
import pers.project.blog.strategy.UploadStrategy;
import pers.project.blog.util.FileIoUtils;

import java.io.InputStream;

/**
 * 抽象上传策略模板
 *
 * @author Luo Fei
 * @date 2023/1/3
 */
public abstract class AbstractUploadStrategyImpl implements UploadStrategy {

    @Override
    public String uploadFile(MultipartFile multipartFile, String directoryUri) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            // 判断文件是否存在，不存在则上传
            String newFileName = FileIoUtils.getNewFileName(multipartFile);
            String fileUri = directoryUri + newFileName;
            if (!exists(fileUri)) {
                upload(inputStream, directoryUri, newFileName);
            }

            return getFileAccessUrl(fileUri);
        } catch (Exception cause) {
            throw new FileUploadException("文件上传失败", cause);
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String directoryUri, String fileName) {
        try {
            upload(inputStream, directoryUri, fileName);

            String fileUri = directoryUri + fileName;
            return getFileAccessUrl(fileUri);
        } catch (Exception cause) {
            throw new FileUploadException("文件上传失败", cause);
        } finally {
            FileIoUtils.close(inputStream);
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param fileUri 文件 Uri
     * @return 存在则返回 true
     */
    public abstract Boolean exists(String fileUri);

    /**
     * 上传
     *
     * @param inputStream  输入流
     * @param directoryUri 上传目录 URI
     * @param fileName     文件名（包含扩展名）
     */
    public abstract void upload(InputStream inputStream, String directoryUri, String fileName) throws Exception;

    /**
     * 获取访问文件的 URL
     *
     * @param fileUri 文件 URI
     * @return 访问文件的 URL
     */
    public abstract String getFileAccessUrl(String fileUri);

}
