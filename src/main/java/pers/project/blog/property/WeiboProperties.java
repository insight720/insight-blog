package pers.project.blog.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微博配置属性
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Data
@ConfigurationProperties(prefix = "blog.login.weibo")
public class WeiboProperties {

    /**
     * 微博 appId
     */
    private String appId;

    /**
     * 微博 appSecret
     */
    private String appSecret;

    /**
     * 微博登录类型
     */
    private String grantType;

    /**
     * 微博回调域名
     */
    private String redirectUrl;

    /**
     * 微博访问令牌地址
     */
    private String accessTokenUrl;

    /**
     * 微博用户信息地址
     */
    private String userInfoUrl;

}
