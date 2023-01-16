package pers.project.blog.handler;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import pers.project.blog.dto.ResourceRoleDTO;
import pers.project.blog.mapper.RoleMapper;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Spring Security 对请求 URL 所需权限的分析处理程序
 *
 * @author Luo Fei
 * @date 2022/12/22
 */
@Component
public class SecurityMetadataSourceHandler implements FilterInvocationSecurityMetadataSource {

    /**
     * 资源角色列表
     */
    private static List<ResourceRoleDTO> resourceRoleList;

    private final RoleMapper roleMapper;

    public SecurityMetadataSourceHandler(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    /**
     * 清空接口角色信息，重新加载
     */
    public static void clearMetadataSource() {
        resourceRoleList = null;
    }

    /**
     * 加载资源角色信息
     */
    @PostConstruct
    private void loadMetadataSource() {
        resourceRoleList = roleMapper.listResourceRoles();
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 修改接口角色关系后重新加载
        if (CollectionUtils.isEmpty(resourceRoleList)) {
            loadMetadataSource();
        }

        // 获取用户请求信息
        FilterInvocation filterInvocation = (FilterInvocation) object;
        String url = filterInvocation.getRequestUrl();
        String requestMethod = filterInvocation.getRequest().getMethod();

        // 获取接口角色信息，若为匿名接口则放行，若无对应角色则禁止
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (ResourceRoleDTO resourceRoleDTO : resourceRoleList) {
            if (antPathMatcher.match(resourceRoleDTO.getUrl(), url)
                    && antPathMatcher.match(resourceRoleDTO.getRequestMethod(), requestMethod)) {
                List<String> roleList = resourceRoleDTO.getRoleList();
                if (CollectionUtils.isEmpty(roleList)) {
                    return SecurityConfig.createList("disable");
                }
                return SecurityConfig.createList(roleList.toArray(new String[]{}));
            }
        }

        return new ArrayList<>();
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    // TODO: 2022/12/24 可能直接返回 true

    /**
     * 与 {@link DefaultFilterInvocationSecurityMetadataSource#supports(Class)}
     * 的实现相同。
     *
     * @param clazz 被查询的类
     * @return {@code true} 如果实现可以处理指定的类
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

}
