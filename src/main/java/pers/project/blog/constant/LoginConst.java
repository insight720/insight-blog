package pers.project.blog.constant;

/**
 * 社交登录常量
 *
 * @author Luo Fei
 * @version 2023/1/15
 */
public abstract class LoginConst {

    // region Weibo

    /**
     * 授权类型
     */
    public static final String GRANT_TYPE = "grant_type";

    /**
     * 微博 UID
     */
    public static final String UID = "uid";

    /**
     * 客户端 ID
     */
    public static final String CLIENT_ID = "client_id";

    /**
     * 客户端密码
     */
    public static final String CLIENT_SECRET = "client_secret";

    /**
     * 验证码
     */
    public static final String CODE = "code";

    /**
     * 回调 URL
     */
    public static final String REDIRECT_URI = "redirect_uri";

    // endregion

    // region QQ

    /**
     * OpenId
     */
    public static final String QQ_OPEN_ID = "openid";

    /**
     * 访问令牌
     */
    public static final String ACCESS_TOKEN = "access_token";

    /**
     * AppId
     */
    public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";

    /**
     * 左括号
     */
    public static final String LEFT_PARENTHESIS = "(";
    /**
     * 右括号
     */
    public static final String RIGHT_PARENTHESIS = "(";

    // endregion

}
