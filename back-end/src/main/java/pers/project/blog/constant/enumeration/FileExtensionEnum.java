package pers.project.blog.constant.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文件扩展名枚举，扩展名不带 "."
 *
 * @author Luo Fei
 * @date 2023/1/4
 */
@Getter
@AllArgsConstructor
public enum FileExtensionEnum {

    /**
     * JPEG 文件
     */
    JPG("jpg", "JPEG 文件"),

    /**
     * JPEG 文件
     */
    JPEG("jpeg", "JPEG 文件"),

    /**
     * PNG 文件
     */
    PNG("png", "PNG 文件"),

    /**
     * WAV文件
     */
    WAV("wav", "WAV 文件"),

    /**
     * Markdown 文件
     */
    MD("md", "Markdown 文件"),

    /**
     * 文本文件
     */
    TXT("txt", "文本文件");

    /**
     * 文件扩展名与其枚举的映射
     */
    private static final Map<String, FileExtensionEnum> FILE_EXTENSION_ENUM_MAP;

    static {
        FILE_EXTENSION_ENUM_MAP = Arrays.stream(FileExtensionEnum.values())
                .collect(Collectors.toMap(FileExtensionEnum::getExtensionName,
                        Function.identity(),
                        (firstEnum, secondEnum) -> firstEnum,
                        () -> CollectionUtils.newHashMap(FileExtensionEnum.values().length)));
    }

    /**
     * 扩展名
     */
    private final String extensionName;

    /**
     * 描述
     */
    private final String description;

    /**
     * 获取文件扩展名枚举
     *
     * @param extensionName 扩展名
     * @return 文件扩展名枚举
     */
    @Nullable
    public static FileExtensionEnum get(String extensionName) {
        return FILE_EXTENSION_ENUM_MAP.get(extensionName);
    }

}
