package pers.project.blog.util;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.RedisConst;
import pers.project.blog.security.BlogAuthorizationManager;
import pers.project.blog.security.BlogUserDetails;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pers.project.blog.constant.GenericConst.*;

/**
 * Spring Security 工具类
 *
 * @author Luo Fei
 * @date 2023/1/27
 */
@Component
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    private static BlogAuthorizationManager authorizationManager;

    private static PasswordEncoder passwordEncoder;

    private static SessionRegistry sessionRegistry;

    @Autowired
    public void setAuthorizationManager(BlogAuthorizationManager authorizationManager) {
        SecurityUtils.authorizationManager = authorizationManager;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        SecurityUtils.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        SecurityUtils.sessionRegistry = sessionRegistry;
    }

    /**
     * 清除 Spring Security 的授权凭据
     * <p>
     * 当需要再次授权时，授权凭据会被重新加载。
     */
    public static void clearAuthorizationCredentials() {
        authorizationManager.updateAuthorizationCredentials();
    }

    /**
     * 验证从存储中获取的编码密码是否与提交的原始密码匹配
     * <p>
     * 如果密码匹配，则返回 true；如果不匹配，则返回 false。存储的密码本身永远不会被解码。
     *
     * @param rawPassword     用于编码和匹配的原始密码 encodedPassword –存储中的编码密码进行比较
     * @param encodedPassword 存储中的编码密码
     * @return 如果编码后的原始密码与存储中的编码密码匹配，则为 true
     */
    public static boolean matches(@NotNull CharSequence rawPassword, @NotNull String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 对原始密码进行编码加密
     *
     * @param rawPassword 用于编码的原始密码
     * @return 用户存储的编码密码
     */
    @NotNull
    public static String encode(@NotNull CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 获取当前 Spring Security 上下文的用户信息
     *
     * @return 用户信息
     */
    @NotNull
    public static BlogUserDetails getUserDetails() {
        return (BlogUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    /**
     * 获取指定主体的所有已知会话。不会返回已销毁的会话。可能会返回已过期的会话，具体取决于传递的参数。
     *
     * @param principal              查找会话的主体（不应为 <code>null<code>）
     * @param includeExpiredSessions 如果<code>为 true，则返回的会话<code>还将包括主体已过期的会话
     * @return 此主体匹配的会话（不返回 null）。
     */
    @NotNull
    public static List<SessionInformation> getAllSession(@NotNull Object principal, boolean includeExpiredSessions) {
        return sessionRegistry.getAllSessions(principal, includeExpiredSessions);
    }

    /**
     * 参见 {@link SecurityUtils#getAllSession(Object, boolean)} 的 Javadoc。
     *
     * @param principal 查找会话的主体（不应为 <code>null<code>）
     * @return 此主体匹配的未过期会话（不返回 null）。
     */
    public static List<SessionInformation> getNonExpiredSessions(@NotNull Object principal) {
        return getAllSession(principal, false);
    }

    /**
     * 判断主体是否没有未过期会话（通过主体的 username 判断）。
     *
     * @param principal 查找会话的主体（不应为 <code>null<code>）
     * @return 如果主体没有其他未过期会话，返回 true；否则返回 false。
     */
    public static boolean noOtherSessions(Object principal) {
        return getNonExpiredSessions(principal).isEmpty();
    }

    /**
     * 判断主体是否有未过期会话（通过主体的 username 判断）。
     *
     * @param principal 查找会话的主体（不应为 <code>null<code>）
     * @return 如果主体有其他未过期会话，返回 true；否则返回 false。
     */
    public static boolean hasOtherSessions(Object principal) {
        return !noOtherSessions(principal);
    }

    /**
     * 获取会话注册表中的所有已知在线用户主体。
     * <p>
     * <b>注意：该方法返回的列表仅包含主体的 username 信息，用于配合使用 Spring Security 的 Api。</b>
     *
     * @return 每个唯一的主体，然后可以呈提供给
     * {@link SecurityUtils#getAllSession(Object, boolean)} 方法。
     */
    public static List<BlogUserDetails> getOnlinePrincipals() {
        return getOnlineUsernames()
                .stream()
                .map(principalName -> BlogUserDetails.builder()
                        .username(principalName)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取所有在线用户的用户名。
     *
     * @return 用户名的集合。
     */
    public static Set<String> getOnlineUsernames() {
        return ConvertUtils.castSet(RedisUtils.sMembers(RedisConst.ONLINE_USERNAME));
    }

    /**
     * 获取当前 Spring Security 上下文用户的指定信息
     *
     * @param function 方法引用
     * @return {@link BlogUserDetails} 的信息
     */
    @NotNull
    public static <T> T getInfo(Function<BlogUserDetails, T> function) {
        return function.apply(getUserDetails());
    }

    /**
     * 获取当前 Spring Security 上下文的用户信息 ID
     *
     * @return 用户信息 ID
     */
    @NotNull
    public static Integer getUserInfoId() {
        return getInfo(BlogUserDetails::getUserInfoId);
    }

    /**
     * 获取随机验证码
     *
     * @return 验证码
     */
    @NotNull
    public static String getRandomCode() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder stringBuilder = new StringBuilder();
        // 6 位随机数字
        for (int i = ZERO; i < SIX; i++) {
            stringBuilder.append(random.nextInt(TEN));
        }
        return stringBuilder.toString();
    }

    /**
     * 获取唯一的名称
     * <p>
     * 使用 19 位数字唯一 ID，
     * <p>
     * 例如："name_id:1621091691538690049"。
     *
     * @return 名称
     */
    @NotNull
    public static String getUniqueName() {
        return "name_id:" + IdWorker.getIdStr();
    }

}
