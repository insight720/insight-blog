package pers.project.blog.security;

import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

/**
 * Spring Security 授权管理器接口
 * <p>
 * 参见 <a href="https://springdoc.cn/spring-security/servlet/authorization/architecture.html#_authorizationmanager">
 * AuthorizationManager</a> 的文档。
 *
 * @author Luo Fei
 * @version 2023/1/27
 */
public interface BlogAuthorizationManager
        extends AuthorizationManager<RequestAuthorizationContext> {

    /**
     * 更新 Spring Security 的授权凭据
     */
    void updateAuthorizationCredentials();

}
