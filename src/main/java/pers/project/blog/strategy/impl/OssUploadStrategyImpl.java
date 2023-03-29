package pers.project.blog.strategy.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.property.UploadProperties;
import pers.project.blog.strategy.AbstractUploadStrategy;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * OSS 上传策略
 *
 * @author Luo Fei
 * @version 2023/03/29
 */
@Component
@EnableConfigurationProperties(UploadProperties.OssUpload.class)
@ConditionalOnProperty(prefix = "blog.upload", name = "strategy", havingValue = "oss")
public class OssUploadStrategyImpl extends AbstractUploadStrategy {

    private final UploadProperties.OssUpload ossUpload;

    private final OSSClient ossClient;

    public OssUploadStrategyImpl(UploadProperties.OssUpload ossUpload) {
        this.ossUpload = ossUpload;
        this.ossClient = getOssClient();
    }

    @Override
    public boolean exists(String fileUri) {
        return ossClient.doesObjectExist(ossUpload.getBucketName(), fileUri);
    }

    @Override
    public void upload(MultipartFile file, InputStream inputStream, String directoryUri, String fileName) throws IOException {
        String bucketName = ossUpload.getBucketName();
        String fileUri = directoryUri + fileName;
        ObjectMetadata metadata = new ObjectMetadata();
        if (file != null) {
            String contentType = file.getContentType();
            if (contentType != null) {
                metadata.setContentType(contentType);
            }
            metadata.setContentLength(file.getSize());
        } else if (inputStream instanceof FileInputStream
                || inputStream instanceof ByteArrayInputStream) {
            // 文件大小可预知
            metadata.setContentLength(inputStream.available());
        }
        ossClient.putObject(bucketName, fileUri, inputStream, metadata);
    }

    @Override
    public String getFileAccessUrl(String fileUri) {
        return ossUpload.getAccessUrl() + fileUri;
    }

    private OSSClient getOssClient() {
        // 可以移动到配置类
        return (OSSClient) new OSSClientBuilder().build(ossUpload.getEndpoint(),
                ossUpload.getAccessKeyId(),
                ossUpload.getAccessKeySecret());
    }

}
