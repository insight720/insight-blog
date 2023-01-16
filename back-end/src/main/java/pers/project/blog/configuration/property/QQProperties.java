package pers.project.blog.configuration.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * QQ 配置属性
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Data
@ConfigurationProperties(prefix = "qq")
public class QQProperties {

    /**
     * QQ appId
     */
    private String appId;

    /**
     * 校验token地址
     */
    private String checkTokenUrl;

    /**
     * QQ用户信息地址
     */
    private String userInfoUrl;

}
