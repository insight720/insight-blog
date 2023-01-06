package pers.project.blog.handler;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security 授权决策处理程序
 *
 * @author Luo Fei
 * @date 2022/12/23
 */
@Component
public class SecurityAccessDecisionHandler implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        // 获取用户权限列表
        Set<String> authotirySet = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // 验证用户权限
        for (ConfigAttribute configAttribute : configAttributes) {
            if (authotirySet.contains(configAttribute.getAttribute())) {
                return;
            }
        }
        throw new AccessDeniedException("没有权限操作");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

}
