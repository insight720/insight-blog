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
import pers.project.blog.property.UploadProperties;
import pers.project.blog.strategy.AbstractUploadStrategy;
import pers.project.blog.util.FileIoUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * OSS 上传策略
 *
 * @author Luo Fei
 * @version 2023/1/4
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
    public Boolean exists(String fileUri) {
        return cosClient.doesObjectExist(cosUpload.getBucketName(), fileUri);
    }

    @Override
    public void upload(InputStream inputStream, String directoryUri, String fileName) throws IOException {
        String bucketName = cosUpload.getBucketName();
        String fileUri = directoryUri + fileName;
        ObjectMetadata metadata = new ObjectMetadata();
        // 输入是可预见长度的流，否则缓冲整个流计算内容长度
        if (inputStream instanceof FileInputStream
                || inputStream instanceof ByteArrayInputStream) {
            metadata.setContentLength(inputStream.available());
        } else {
            byte[] contentBytes = FileIoUtils.readBytes(inputStream);
            metadata.setContentLength(contentBytes.length);
            inputStream = new ByteArrayInputStream(contentBytes);
        }
        cosClient.putObject(bucketName, fileUri, inputStream, metadata);
    }

    @Override
    public String getFileAccessUrl(String fileUri) {
        return cosUpload.getAccessUrl() + fileUri;
    }

    private COSClient getCosClient() {
        COSCredentials cosCredentials = new BasicCOSCredentials
                (cosUpload.getSecretId(), cosUpload.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(cosUpload.getRegion()));
        return new COSClient(cosCredentials, clientConfig);
    }

}
