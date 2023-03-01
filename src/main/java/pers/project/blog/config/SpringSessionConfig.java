package pers.project.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import pers.project.blog.property.SessionProperties;
import pers.project.blog.util.StrRegexUtils;

import javax.annotation.Resource;

/**
 * Spring Session 配置类
 *
 * @author Luo Fei
 * @date 2023/2/7
 */
@Slf4j
@Configuration
@EnableRedisHttpSession
public class SpringSessionConfig {

    @Resource
    private SessionProperties properties;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(properties.getCookieName());
        serializer.setCookieMaxAge(properties.getCookieMaxAge());
        String domainName = properties.getDomainName();
        if (log.isWarnEnabled() && StrRegexUtils.isNotBlank(domainName)) {
            log.warn("Session 域名设置为 {}，测试环境不要设置域名，否则 Session 会失效", domainName);
            serializer.setDomainName(domainName);
        }
        serializer.setCookiePath(properties.getCookiePath());
        return serializer;
    }

    // 不能配置 JSON 序列化，Spring Security 框架内的组件存在 JSON 序列化问题

}

