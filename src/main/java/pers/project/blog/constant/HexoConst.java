package pers.project.blog.constant;

/**
 * Hexo 文章常量
 *
 * @author Luo Fei
 * @version 2023/1/8
 */
public abstract class HexoConst {

    /**
     * 标题
     */
    public static final String TITLE_PREFIX = "title:";

    /**
     * 日期时间
     */
    public static final String DATE_PREFIX = "date:";

    /**
     * 分类
     */
    public static final String CATEGORIES_PREFIX = "categories:";

    /**
     * 标签
     */
    public static final String TAGS_PREFIX = "tags:";

    /**
     * 标签或类别名称前缀
     */
    public static final String CATEGORY_OR_TAG_NAME_PREFIX = "-";

    /**
     * 初始标记
     */
    public static final Integer NORMAL_FLAG = 0;

    /**
     * 分类标记
     */
    public static final Integer CATEGORY_FLAG = 1;

    /**
     * 标签标记
     */
    public static final Integer TAG_FLAG = 2;

    /**
     * LF 换行符
     */
    public static final String LINE_FEED = "\n";

    /**
     * 分隔符
     */
    public static final String DELIMITER = "---";

    /**
     * 最大分隔符计数
     */
    public static final Integer MAX_DELIMITER_COUNT = 2;

}
