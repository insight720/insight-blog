package pers.project.blog.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Session 配置属性
 *
 * @author Luo Fei
 * @date 2023/2/8
 */
@Data
@Component
@ConfigurationProperties(prefix = "blog.session")
public class SessionProperties {

    /**
     * 会话 Cookie 名（默认为 SESSION)
     */
    private String cookieName = "SESSION";

    /**
     * 域名（当前域名或更高级的域名，测试环境不要设置域名！）
     */
    private String domainName;

    /**
     * 会话作用域（默认为根目录）
     */
    private String cookiePath = "/";

    /**
     * 会话过期时间（单位秒，默认为 30 分钟）
     */
    private Integer cookieMaxAge = 30 * 60;

}
