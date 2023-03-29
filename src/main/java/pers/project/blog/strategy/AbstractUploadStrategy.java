package pers.project.blog.strategy;

import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.util.FileIoUtils;

import java.io.InputStream;

/**
 * 抽象上传策略模板
 *
 * @author Luo Fei
 * @version 2023/03/29
 */
public abstract class AbstractUploadStrategy implements UploadStrategy {

    @Override
    public String uploadFile(MultipartFile multipartFile, String directoryUri) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            // 判断文件是否存在，不存在则上传
            String newFileName = FileIoUtils.getNewFileName(multipartFile);
            String fileUri = directoryUri + newFileName;
            if (!exists(fileUri)) {
                upload(multipartFile, inputStream, directoryUri, newFileName);
            }
            return getFileAccessUrl(fileUri);
        } catch (Exception cause) {
            throw new ServiceException("文件上传异常", cause);
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String directoryUri, String fileName) {
        try {
            String fileUri = directoryUri + fileName;
            // 不调用 exist，fileUri 可能重复但内容不同
            upload(null, inputStream, directoryUri, fileName);
            return getFileAccessUrl(fileUri);
        } catch (Exception cause) {
            throw new ServiceException("文件上传异常", cause);
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
    public abstract boolean exists(String fileUri);

    /**
     * 上传
     * <p>
     * 为了修正错误并避免改动太大，file 参数是后加的，所以 file 和 inputStream 只用其中一个。
     * <p>
     * 改动后该方法抽取得不太好，如果重写可以重新设计抽象结构。
     *
     * @param file         上传的文件（可能为 null）
     * @param inputStream  输入流（不为 null）
     * @param directoryUri 上传目录 URI
     * @param fileName     文件名（包含扩展名）
     */
    public abstract void upload(MultipartFile file, InputStream inputStream, String directoryUri, String fileName) throws Exception;

    /**
     * 获取访问文件的 URL
     *
     * @param fileUri 文件 URI
     * @return 访问文件的 URL
     */
    public abstract String getFileAccessUrl(String fileUri);

}
