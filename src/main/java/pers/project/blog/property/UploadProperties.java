package pers.project.blog.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 上传属性配置
 * <p>
 * <b>注意：所有路径相关属性末尾不带分隔符</b>
 *
 * @author Luo Fei
 * @version 2023/1/4
 */
@Data
@ConfigurationProperties(prefix = "blog.upload")
public class UploadProperties {

    /**
     * 上传策略
     * <pre>
     * local 本地（需要配合 Nginx 使用）
     * cos   腾讯云对象存储
     * oos   阿里云对像存储服务
     * </pre>
     */
    private String strategy;

    /**
     * 本地上传属性
     */
    @Data
    @ConfigurationProperties(prefix = "blog.upload.local")
    public static class LocalUpload {

        /**
         * 本地文件存储路径（以分隔符结尾）
         */
        private String uploadUrl;

        /**
         * 本地文件存储路径访问 URL（以分隔符结尾）
         */
        private String accessUrl;

    }

    /**
     * OSS 上传属性
     */
    @Data
    @ConfigurationProperties(prefix = "blog.upload.oss")
    public static class OssUpload {

        /**
         * 存储路径访问 URL（以分隔符结尾）
         */
        private String accessUrl;

        /**
         * 端点
         */
        private String endpoint;

        /**
         * 存储空间名称
         */
        private String bucketName;

        /**
         * 访问密钥 ID
         */
        private String accessKeyId;

        /**
         * 访问密钥密码
         */
        private String accessKeySecret;

    }

    /**
     * COS 上传属性
     */
    @Data
    @ConfigurationProperties(prefix = "blog.upload.cos")
    public static class CosUpload {

        /**
         * 存储路径访问 URL（以分隔符结尾）
         */
        private String accessUrl;

        /**
         * 区域
         */
        private String region;

        /**
         * 存储空间名称
         */
        private String bucketName;

        /**
         * 密钥 ID
         */
        private String secretId;

        /**
         * 密钥
         */
        private String secretKey;

    }

}
