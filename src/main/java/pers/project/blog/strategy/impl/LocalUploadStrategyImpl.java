package pers.project.blog.strategy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.enums.FileExtensionEnum;
import pers.project.blog.property.UploadProperties;
import pers.project.blog.strategy.AbstractUploadStrategy;
import pers.project.blog.util.FileIoUtils;

import java.io.*;
import java.nio.file.Files;


/**
 * 本地上传策略
 *
 * @author Luo Fei
 * @version 2023/03/29
 */
@Component
@ConditionalOnProperty(prefix = "blog.upload", name = "strategy", havingValue = "local")
@EnableConfigurationProperties(UploadProperties.LocalUpload.class)
public class LocalUploadStrategyImpl extends AbstractUploadStrategy {

    private final UploadProperties.LocalUpload localUploadProperties;

    @Autowired
    public LocalUploadStrategyImpl(UploadProperties.LocalUpload localUploadProperties) {
        this.localUploadProperties = localUploadProperties;
    }

    @Override
    public boolean exists(String fileUri) {
        String fileUrl = localUploadProperties.getUploadUrl() + fileUri;
        return new File(fileUrl).exists();
    }

    @Override
    public void upload(MultipartFile multipartFile, InputStream inputStream,
                       String directoryUri, String fileName) throws Exception {
        // 判断目录是否存在，不存在则创建
        String directoryUrl = localUploadProperties.getUploadUrl() + directoryUri;
        File directory = new File(directoryUrl);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("创建目录异常");
        }
        // 根据文件类型上传文件
        String fileUrl = directoryUrl + fileName;
        File file = new File(fileUrl);
        FileExtensionEnum extensionEnum = FileExtensionEnum.get
                (FileIoUtils.getExtensionName(fileName));
        switch (extensionEnum) {
            case MD:
            case TXT:
                try (BufferedReader reader
                             = new BufferedReader(new InputStreamReader(inputStream));
                     BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    FileIoUtils.copyCharStream(reader, writer);
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
