package pers.project.blog.constant;

import static pers.project.blog.constant.FileIoConstant.SEPARATOR;

/**
 * 文件上路目录 URI 枚举（不以分隔符开始，以分隔符结尾）
 *
 * @author Luo Fei
 * @date 2023/1/5
 */
public abstract class DirectoryUriConstant {

    /**
     * 文章图片目录
     */
    public static final String ARTICLE = "article" + SEPARATOR;

    /**
     * 头像目录
     */
    public static final String AVATAR = "avatar" + SEPARATOR;

    /**
     * 配置图片目录
     */
    public static final String CONFIG = "config" + SEPARATOR;

    /**
     * Markdown 文件目录
     */
    public static final String MARKDOWN = "markdown" + SEPARATOR;

    /**
     * 照片目录
     */
    public static final String PHOTO = "photo" + SEPARATOR;

    /**
     * 说说图片目录
     */
    public static final String TALK = "talks" + SEPARATOR;

    /**
     * 语音目录
     */
    public static final String VOICE = "voice" + SEPARATOR;

}
