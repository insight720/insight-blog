package pers.project.blog.security;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pers.project.blog.entity.UserAuth;
import pers.project.blog.entity.UserInfo;
import pers.project.blog.mapper.RoleMapper;
import pers.project.blog.mapper.UserAuthMapper;
import pers.project.blog.mapper.UserInfoMapper;
import pers.project.blog.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static pers.project.blog.constant.GenericConst.*;
import static pers.project.blog.constant.RedisConst.*;

/**
 * Spring Security 加载用户特定数据的 Service 实现
 * <p>
 * 参见 <a href="https://springdoc.cn/spring-security/servlet/authentication/passwords/user-details-service.html">
 * UserDetailsService</a> 文档。
 *
 * @author Luo Fei
 * @version 2022/12/25
 */
@Service
public class BlogUserDetailsServiceImpl implements BlogUserDetailsService {

    @Resource
    private UserAuthMapper userAuthMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户账号是否存在
        if (StrRegexUtils.isBlank(username)) {
            // 该异常默认被隐藏，抛出 BadCredentialsException
            throw new UsernameNotFoundException("用户名不能为空");
        }
        UserAuth userAuth = new LambdaQueryChainWrapper<>(userAuthMapper)
                .select(UserAuth::getId, UserAuth::getUserInfoId,
                        UserAuth::getUsername, UserAuth::getPassword,
                        UserAuth::getLoginType)
                .eq(UserAuth::getUsername, username).one();
        if (userAuth == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        // 加载用户信息
        return loadUserDetails(userAuth);
    }

    @Override
    @SuppressWarnings("all")
    public BlogUserDetails loadUserDetails(UserAuth userAuth) {
        // 查询用户点赞信息
        Integer userInfoId = userAuth.getUserInfoId();
        Supplier<List> likeSetSupplier = () ->
                RedisUtils.executePipelined(new SessionCallback<Object>() {
                    @Override
                    public Object execute(RedisOperations operations) {
                        SetOperations opsForSet = operations.opsForSet();
                        opsForSet.members(ARTICLE_LIKE_PREFIX + userInfoId);
                        opsForSet.members(COMMENT_LIKE_PREFIX + userInfoId);
                        opsForSet.members(TALK_LIKE_PREFIX + userInfoId);
                        return null;
                    }
                });
        CompletableFuture<List> likeSetListFuture = AsyncUtils.supplyAsync(likeSetSupplier);
        // 查询用户账号信息
        UserInfo userInfo = userInfoMapper.selectById(userInfoId);
        // 获取用户代理信息
        HttpServletRequest request = WebUtils.getCurrentRequest();
        UserAgent userAgent = WebUtils.getUserAgent(request);
        String browser = userAgent.getBrowser().getName();
        String os = userAgent.getOperatingSystem().getName();
        // 获取登录 IP 信息
        String ipAddress = WebUtils.getIpAddress(request);
        String ipSource = WebUtils.getIpSource(ipAddress);
        // 查询用户权限角色
        List<String> roleList = roleMapper.listAuthorityRoles(userInfoId);
        // 获取点赞信息查询结果
        List<Set<Object>> likeSetList = AsyncUtils.get
                (likeSetListFuture, "查询用户点赞信息异常");
        Set<Object> articleLikeSet = likeSetList.get(ZERO);
        Set<Object> commentLikeSet = likeSetList.get(ONE);
        Set<Object> talkLikeSet = likeSetList.get(TWO);
        // 封装用户信息
        return BlogUserDetails.builder()
                .id(userAuth.getId())
                .userInfoId(userInfoId)
                .username(userAuth.getUsername())
                .password(userAuth.getPassword())
                .loginType(userAuth.getLoginType())
                .ipAddress(ipAddress)
                .ipSource(ipSource)
                .lastLoginTime(TimeUtils.now())
                .email(userInfo.getEmail())
                .nickname(userInfo.getNickname())
                .avatar(userInfo.getAvatar())
                .intro(userInfo.getIntro())
                .webSite(userInfo.getWebSite())
                .isDisable(userInfo.getIsDisable())
                .browser(browser)
                .os(os)
                .roleList(roleList)
                .articleLikeSet(articleLikeSet)
                .commentLikeSet(commentLikeSet)
                .talkLikeSet(talkLikeSet)
                .build();
    }

}
