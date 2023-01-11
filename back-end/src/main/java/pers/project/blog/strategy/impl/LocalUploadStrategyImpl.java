package pers.project.blog.strategy.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import pers.project.blog.configuration.property.UploadProperties;
import pers.project.blog.constant.enumeration.FileExtensionEnum;
import pers.project.blog.exception.FileUploadException;
import pers.project.blog.strategy.AbstractUploadStrategy;
import pers.project.blog.util.FileIoUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;


/**
 * 本地上传策略
 *
 * @author Luo Fei
 * @date 2023/1/4
 */
@Component
@ConditionalOnProperty(prefix = "blog.upload", name = "strategy", havingValue = "local")
@EnableConfigurationProperties(UploadProperties.LocalUpload.class)
public class LocalUploadStrategyImpl extends AbstractUploadStrategy {

    private final UploadProperties.LocalUpload localUploadProperties;

    public LocalUploadStrategyImpl(UploadProperties.LocalUpload localUploadProperties) {
        this.localUploadProperties = localUploadProperties;
    }

    @Override
    public Boolean exists(String fileUri) {
        String fileUrl = localUploadProperties.getUploadUrl() + fileUri;
        return new File(fileUrl).exists();
    }

    @Override
    public void upload(InputStream inputStream, String directoryUri, String fileName) throws IOException {
        // 判断目录是否存在，不存在则创建
        String directoryUrl = localUploadProperties.getUploadUrl() + directoryUri;
        File directory = new File(directoryUrl);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new FileUploadException("创建目录失败");
        }

        // 根据文件类型上传文件
        String fileUrl = directoryUrl + fileName;
        File file = new File(fileUrl);
        switch (Objects.requireNonNull(FileExtensionEnum.get
                (FileIoUtils.getExtensionName(fileName)), "无扩展名")) {
            case MD:
            case TXT:
                try (BufferedReader bufferedReader
                             = new BufferedReader(new InputStreamReader(inputStream));
                     BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                    FileIoUtils.copyCharStream(bufferedReader, bufferedWriter);
                }
                break;
            default:
                try (BufferedInputStream bis = new BufferedInputStream(inputStream);
                     BufferedOutputStream bos
                             = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
                    FileIoUtils.copyByteStream(bis, bos);
                }
        }
    }

    @Override
    public String getFileAccessUrl(String fileUri) {
        return localUploadProperties.getAccessUrl() + fileUri;
    }

}
