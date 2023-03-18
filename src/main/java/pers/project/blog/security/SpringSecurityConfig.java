package pers.project.blog.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import pers.project.blog.property.SessionProperties;

import javax.annotation.Resource;

import static pers.project.blog.security.SecurityConst.*;

/**
 * Spring Security 配置类
 * <p>
 * 参见 <a href="https://springdoc.cn/spring-security/">
 * Spring Security</a> 中文文档。
 *
 * @author Luo Fei
 * @version 2022/12/22
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    /**
     * Spring Security 默认使用
     * <a href="https://springdoc.cn/spring-security/features/authentication/password-storage.html#authentication-password-storage-dpe">
     * DelegatingPasswordEncoder</a>。
     * <p>
     * 可以通过将 PasswordEncoder 暴露为 Spring Bean 来进行定制。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 会话注册表
     * <p>
     * Spring Session 的实现无法使用 getAllPrincipals()。
     * <p>
     * 参见 <a href="https://springdoc.cn/spring-security/servlet/authentication/session-management.html#list-authenticated-principals">
     * SessionRegistry</a> 的文档。
     * <p>
     * 参见<a href="https://docs.spring.io/spring-session/reference/spring-security.html#spring-security-concurrent-sessions-limitations">
     * Spring Security Integration</a> 的文档。
     */
    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
    public SessionRegistry sessionRegistry(FindByIndexNameSessionRepository sessionRepository) {
        return new SpringSessionBackedSessionRegistry(sessionRepository);
    }

    /**
     * Spring Security 会话事件的发布者
     * <p>
     * 暴露为 Spring Bean 来保持对会话生命周期事件的更新。
     * <p>
     * 参见 <a href="https://springdoc.cn/spring-security/servlet/authentication/session-management.html#ns-concurrent-sessions">
     * HttpSessionEventPublisher</a> 的文档。
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * 自定义 WebSecurity 的回调接口
     * <p>
     * 用于配置 Spring Security 忽略的资源请求。
     * <p>
     * 参见 {@link WebSecurity} 的 Javadoc。
     */
    @Bean
    public WebSecurityCustomizer ignoringCustomizer() {
        return (web) -> web.ignoring().mvcMatchers(IGNORING_PATTENS);
    }

    @Configuration
    public static class SecurityFilterChainConfig {

        // region 认证
        @Resource
        private AuthenticationSuccessHandler authenticationSuccessHandler;
        @Resource
        private AuthenticationFailureHandler authenticationFailureHandler;
        @Resource
        private LogoutSuccessHandler logoutSuccessHandler;
        @Resource
        private AuthenticationEntryPoint authenticationEntryPoint;
        // endregion

        // region 授权
        @Resource
        private BlogAuthorizationManager authorizationManager;
        @Resource
        private AccessDeniedHandler accessDeniedHandler;
        // endregion

        // region 会话管理
        @Resource
        private SessionRegistry sessionRegistry;
        @Resource
        private SessionProperties properties;
        // endregion

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            // 关闭 CSRF 防护（CSRF 防护需要前端配合）
            http.csrf().disable();
            // 配置表单登录
            http.formLogin()
                    .loginProcessingUrl(LOGIN_PROCESSING_URL)
                    .successHandler(authenticationSuccessHandler)
                    .failureHandler(authenticationFailureHandler);
            // 配置登出
            http.logout()
                    .logoutUrl(LOGOUT_URL)
                    .deleteCookies(properties.getCookieName())
                    .logoutSuccessHandler(logoutSuccessHandler);
            // 配置授权管理器
            http.authorizeHttpRequests()
                    .anyRequest()
                    .access(authorizationManager);
            // 配置认证异常和授权异常处理
            http.exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler);
            // 配置会话管理
            http.sessionManagement()
                    .maximumSessions(MAXIMUM_SESSIONS)
                    .sessionRegistry(sessionRegistry);
            return http.build();
        }
    }

}
