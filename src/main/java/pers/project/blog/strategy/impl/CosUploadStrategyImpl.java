package pers.project.blog.strategy.impl;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
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
@EnableConfigurationProperties(UploadProperties.CosUpload.class)
@ConditionalOnProperty(prefix = "blog.upload", name = "strategy", havingValue = "cos")
public class CosUploadStrategyImpl extends AbstractUploadStrategy {

    private final UploadProperties.CosUpload cosUpload;

    private final COSClient cosClient;

    @Autowired
    public CosUploadStrategyImpl(UploadProperties.CosUpload cosUpload) {
        this.cosUpload = cosUpload;
        this.cosClient = getCosClient();
    }

    @Override
    public boolean exists(String fileUri) {
        return cosClient.doesObjectExist(cosUpload.getBucketName(), fileUri);
    }

    @Override
    public void upload(MultipartFile file, InputStream inputStream,
                       String directoryUri, String fileName) throws IOException {
        String bucketName = cosUpload.getBucketName();
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
        cosClient.putObject(bucketName, fileUri, inputStream, metadata);
    }

    @Override
    public String getFileAccessUrl(String fileUri) {
        return cosUpload.getAccessUrl() + fileUri;
    }

    private COSClient getCosClient() {
        // COS 客户端只能单例使用，可以移动到配置类
        COSCredentials cosCredentials = new BasicCOSCredentials
                (cosUpload.getSecretId(), cosUpload.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(cosUpload.getRegion()));
        return new COSClient(cosCredentials, clientConfig);
    }

}
