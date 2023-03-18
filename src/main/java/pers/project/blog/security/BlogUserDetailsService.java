package pers.project.blog.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import pers.project.blog.entity.UserAuth;

/**
 * Spring Security 加载用户特定数据的 Service
 * <p>
 * 参见 <a href="https://springdoc.cn/spring-security/servlet/authentication/passwords/user-details-service.html">
 * UserDetailsService</a> 文档。
 *
 * @author Luo Fei
 * @version 2023/1/27
 */
public interface BlogUserDetailsService extends UserDetailsService {

    /**
     * 加载 Spring Security 用户信息
     *
     * @param userAuth 用户认证信息
     * @return Spring Security 用户信息
     */
    BlogUserDetails loadUserDetails(UserAuth userAuth);

}
