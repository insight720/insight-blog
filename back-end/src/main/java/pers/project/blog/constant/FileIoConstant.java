package pers.project.blog.constant;

/**
 * 文件 IO 相关常量类
 *
 * @author Luo Fei
 * @date 2023/1/4
 */
public abstract class FileIoConstant {

    /**
     * 点
     */
    public static final String DOT = ".";
    // TODO: 2023/1/4 windows 存储要改路径分隔符

    /**
     * 类 Unix 路径分隔符
     */
    public static final String UNIX_SEPARATOR = "/";

    /**
     * Windows路径分隔符
     */
    public static final String WINDOWS_SEPARATOR = "\\";

    /**
     * 路径分隔符
     */
    public static final String SEPARATOR = UNIX_SEPARATOR;

    /**
     * 默认缓存区大小
     */
    public static final int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * 数据流末尾
     */
    public static final int EOF = -1;

}
