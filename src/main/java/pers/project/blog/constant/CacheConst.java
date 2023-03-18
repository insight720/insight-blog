package pers.project.blog.constant;

/**
 * 缓存常量
 *
 * @author Luo Fei
 * @version 2023/1/30
 */
public abstract class CacheConst {

    /**
     * 缓存前缀
     */
    public static final String CACHE_NAME_PREFIX = "cache:";

    // region cacheNames

    /**
     * 包含隐藏状态的菜单缓存组
     */
    public static final String MENU_WITH_HIDDEN = "menu_with_hidden";

    /**
     * 不包含隐藏状态的菜单缓存组
     */
    public static final String MENU_WITHOUT_HIDDEN = "menu_without_hidden";

    /**
     * 权限角色缓存组
     */
    public static final String ROLE = "role";

    /**
     * 资源缓存组
     */
    public static final String RESOURCE = "resource";

    /**
     * 说说缓存组
     */
    public static final String TALK = "talk";

    /**
     * 标签缓存组
     */
    public static final String TAG = "tag";

    /**
     * 相册缓存组
     */
    public static final String PHOTO_ALBUM = "photo_album";

    /**
     * 页面缓存组
     */
    public static final String PAGE = "page";

    /**
     * 友链缓存组
     */
    public static final String FRIEND_LINK = "friend_link";

    // endregion

}
