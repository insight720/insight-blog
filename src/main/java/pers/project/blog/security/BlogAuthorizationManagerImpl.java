package pers.project.blog.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import pers.project.blog.dto.role.ResourceRoleDTO;
import pers.project.blog.mapper.RoleMapper;
import pers.project.blog.util.ConvertUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static pers.project.blog.constant.GenericConst.TRUE_OF_INT;

/**
 * Spring Security 授权管理器实现
 * <p>
 * 参见 <a href="https://springdoc.cn/spring-security/servlet/authorization/architecture.html#_authorizationmanager">
 * AuthorizationManager</a> 的文档。
 *
 * @author Luo Fei
 * @date 2023/1/26
 */
@Component
public class BlogAuthorizationManagerImpl implements BlogAuthorizationManager {

    /**
     * 是否请求匿名资源
     */
    private static final ThreadLocal<Boolean> ANONYMOUS = new ThreadLocal<>();

    private static final ReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock(true);
    private static final Lock READ_LOCK = READ_WRITE_LOCK.readLock();
    private static final Lock WRITE_LOCK = READ_WRITE_LOCK.writeLock();

    private static volatile boolean invalid;

    /**
     * 接口资源和授权角色的信息列表
     * <p>
     * 用作授权的依据，volatile 和公平读写锁保证了线程安全。
     */
    private static List<ResourceRoleDTO> resourceRoleList;

    @Resource
    private RoleMapper roleMapper;

    private static void clearResourceRoleList() {
        WRITE_LOCK.lock();
        try {
            resourceRoleList = null;
            invalid = true;
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    @PostConstruct
    private void loadResourceRoleList() {
        resourceRoleList = roleMapper.listResourceRoles();
    }

    @Override
    public void updateAuthorizationCredentials() {
        clearResourceRoleList();
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
                                       RequestAuthorizationContext requestContext) {
        boolean authorized;
        ANONYMOUS.set(FALSE);
        try {
            // 获取用户权限列表
            List<SimpleGrantedAuthority> grantedList = ConvertUtils.castList
                    (authentication.get().getAuthorities());
            // 获取可用权限列表
            Set<SimpleGrantedAuthority> availableSet
                    = getAvailableAuthorities(requestContext);
            // 判断是否授权
            authorized = ANONYMOUS.get()
                    || grantedList.stream().anyMatch(availableSet::contains);
        } finally {
            ANONYMOUS.remove();
        }
        return new AuthorizationDecision(authorized);
    }

    private Set<SimpleGrantedAuthority> getAvailableAuthorities
            (RequestAuthorizationContext requestContext) {
        READ_LOCK.lock();
        if (invalid) {
            // Must release read lock before acquiring write lock.
            READ_LOCK.unlock();
            WRITE_LOCK.lock();
            // Recheck state because another thread might have
            // acquired write lock and changed state before we did.
            try {
                if (invalid) {
                    loadResourceRoleList();
                    invalid = false;
                }
                // Downgrade by acquiring read lock before releasing write lock
                READ_LOCK.lock();
            } finally {
                // Unlock write, still hold read
                WRITE_LOCK.unlock();
            }
        }
        // Read safely
        Set<SimpleGrantedAuthority> availableAuthorities;
        try {
            availableAuthorities = processResourceRoleList(requestContext);
        } finally {
            READ_LOCK.unlock();
        }
        return availableAuthorities;
    }

    private Set<SimpleGrantedAuthority> processResourceRoleList
            (RequestAuthorizationContext requestContext) {
        // 获取用户请求信息
        HttpServletRequest request = requestContext.getRequest();
        String url = request.getRequestURI();
        String method = request.getMethod();
        // 确定用户请求的可用权限
        AntPathMatcher matcher = new AntPathMatcher();
        for (ResourceRoleDTO resource : resourceRoleList) {
            if (matcher.match(resource.getUrl(), url)
                    && matcher.match(resource.getRequestMethod(), method)) {
                List<String> roleList = resource.getRoleList();
                // 匿名可访问资源
                if (TRUE_OF_INT.equals(resource.getIsAnonymous())) {
                    ANONYMOUS.set(TRUE);
                }
                return roleList.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());
            }
        }
        return new HashSet<>();
    }

}
