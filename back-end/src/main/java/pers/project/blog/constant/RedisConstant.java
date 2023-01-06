package pers.project.blog.constant;

/**
 * Redis 相关常量
 *
 * @author Luo Fei
 * @date 2022/12/26
 */
public abstract class RedisConstant {

    /**
     * 用户点赞文章
     */
    public static final String ARTICLE_USER_LIKE = "article_user_like:";

    /**
     * 用户点赞评论
     */
    public static final String COMMENT_USER_LIKE = "comment_user_like:";

    /**
     * 用户点赞说说
     */
    public static final String TALK_USER_LIKE = "talk_user_like:";

    /**
     * 访客
     */
    public static final String UNIQUE_VISITOR = "unique_visitor";

    /**
     * 访客地区
     */
    public static final String VISITOR_AREA = "visitor_area";

    /**
     * 博客浏览量
     */
    public static final String BLOG_VIEWS_COUNT = "blog_views_count";

    /**
     * 用户地区
     */
    public static final String USER_AREA = "user_area";

    /**
     * 文章浏览量
     */
    public static final String ARTICLE_VIEWS_COUNT = "article_views_count";

    /**
     * 网站配置
     */
    public static final String WEBSITE_CONFIG = "website_config";

    /**
     * 页面封面
     */
    public static final String PAGE_COVER = "page_cover";

    /**
     * 关于我信息
     */
    public static final String ABOUT = "about";

}
