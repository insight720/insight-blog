package pers.project.blog.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import pers.project.blog.handler.*;

/**
 * Spring Security 配置类
 *
 * @author Luo Fei
 * @date 2022/12/22
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final SecurityAuthenticationSuccessHandler authenticationSuccessHandler;

    private final SecurityAuthenticationFailureHandler authenticationFailureHandler;

    private final SecurityLogoutSuccessHandler logoutSuccessHandler;

    private final SecurityAccessDecisionHandler accessDecisionHandler;

    private final SecurityMetadataSourceHandler metadataSourceHandler;

    private final SecurityAuthenticationExceptionHandler authenticationExceptionHandler;

    private final SecurityAccessDeniedExceptionHandler accessDeniedExceptionHandler;

    public SpringSecurityConfiguration(SecurityAuthenticationSuccessHandler authenticationSuccessHandler,
                                       SecurityAuthenticationFailureHandler authenticationFailureHandler,
                                       SecurityLogoutSuccessHandler logoutSuccessHandler,
                                       SecurityAccessDecisionHandler accessDecisionHandler,
                                       SecurityMetadataSourceHandler metadataSourceHandler,
                                       SecurityAuthenticationExceptionHandler authenticationExceptionHandler,
                                       SecurityAccessDeniedExceptionHandler accessDeniedExceptionHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.accessDecisionHandler = accessDecisionHandler;
        this.metadataSourceHandler = metadataSourceHandler;
        this.authenticationExceptionHandler = authenticationExceptionHandler;
        this.accessDeniedExceptionHandler = accessDeniedExceptionHandler;
    }

    /**
     * 密码加密
     *
     * @return {@link PasswordEncoder} 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 维护 Spring Security 框架内的会话记录
     *
     * @return {@link SessionRegistry}
     * <p>
     * 维护 {@link SessionInformation} 实例的注册表
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * Session 过期时通知 Spring Security 框架
     *
     * @return {@link HttpSessionEventPublisher} Session 事件的发布者
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }


    // TODO: 2022/12/28 测试用
    /*
     * 测试使用，绕过 Spring Security 过滤器链
     */
    @Override
    public void configure(WebSecurity web) {
//        web.ignoring().antMatchers("/**");
    }

    /**
     * 配置 Spring Security 框架的认证和授权
     *
     * @param http 它能为特定的 Http 请求配置权限规则
     * @throws Exception 如果在构建对象时发生错误
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 【关闭 CSRF（跨站请求伪造）防护】
        http.csrf().disable();

        // 配置【授权决策处理程序】和【对请求 URL 所需权限的分析处理程序】
        http.authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O filterSecurityInterceptor) {
                        filterSecurityInterceptor.setAccessDecisionManager(accessDecisionHandler);
                        filterSecurityInterceptor.setSecurityMetadataSource(metadataSourceHandler);
                        return filterSecurityInterceptor;
                    }
                })
                .anyRequest()
                .permitAll();

        // 配置【用户登录、注销的 URL 和处理程序】
        http.formLogin()
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler);

        // 配置【用户未登录或权限不足的处理程序】
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationExceptionHandler)
                .accessDeniedHandler(accessDeniedExceptionHandler);

        // TODO: 2022/12/25 单个用户会话最大数量可能不需要这么大
        // 配置【Session 管理】
        http.sessionManagement()
                .maximumSessions(20)
                .sessionRegistry(sessionRegistry());
    }

}
