package pers.project.blog.strategy.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import pers.project.blog.configuration.property.UploadProperties;
import pers.project.blog.strategy.AbstractUploadStrategy;

import java.io.InputStream;

/**
 * OSS 上传策略
 *
 * @author Luo Fei
 * @date 2023/1/4
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
    public Boolean exists(String fileUri) {
        return ossClient.doesObjectExist(ossUpload.getBucketName(), fileUri);
    }

    @Override
    public void upload(InputStream inputStream, String directoryUri, String fileName) {
        String fileUri = directoryUri + fileName;
        ossClient.putObject(ossUpload.getBucketName(), fileUri, inputStream);
    }

    @Override
    public String getFileAccessUrl(String fileUri) {
        return ossUpload.getAccessUrl() + fileUri;
    }

    private OSSClient getOssClient() {
        return (OSSClient) new OSSClientBuilder().build(ossUpload.getEndpoint(),
                ossUpload.getAccessKeyId(),
                ossUpload.getAccessKeySecret());
    }

}
