package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pers.project.blog.constant.RedisConstant;
import pers.project.blog.dto.UserDetailsDTO;
import pers.project.blog.entity.UserAuthEntity;
import pers.project.blog.entity.UserInfoEntity;
import pers.project.blog.mapper.RoleMapper;
import pers.project.blog.mapper.UserAuthMapper;
import pers.project.blog.mapper.UserInfoMapper;
import pers.project.blog.util.IpUtils;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.util.TimeUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * Spring Security 认证功能的 Service 实现
 *
 * @author Luo Fei
 * @date 2022/12/25
 */
@Primary
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static UserInfoMapper userInfoMapper;
    private static RoleMapper roleMapper;
    private HttpServletRequest httpServletRequest;
    private UserAuthMapper userAuthMapper;

    // TODO: 2023/1/15 注入问题

    /**
     * 创建 {@link UserDetailsDTO} 对象
     *
     * @param userAuthEntity 用户认证信息
     * @param request        用户请求
     * @return {@link UserDetailsDTO} Spring Security 使用的用户信息建模
     */
    public static UserDetailsDTO createUserDetails(UserAuthEntity userAuthEntity, HttpServletRequest request) {
        // 查询用户账号信息
        UserInfoEntity userInfoEntity = userInfoMapper.selectById(userAuthEntity.getUserInfoId());

        // 查询用户角色
        List<String> roleLabelList = roleMapper.listRoleLabelsByUserInfoId(userInfoEntity.getId());

        // 查询账号点赞信息
        Set<Object> articleLikeSet = RedisUtils.sMembers(RedisConstant.ARTICLE_USER_LIKE + userInfoEntity.getId());
        Set<Object> commentLikeSet = RedisUtils.sMembers(RedisConstant.COMMENT_USER_LIKE + userInfoEntity.getId());
        Set<Object> talkLikeSet = RedisUtils.sMembers(RedisConstant.TALK_USER_LIKE + userInfoEntity.getId());
// TODO: 2023/1/15 重复计算 IP
        // 获取设备信息
        String ipAddress = IpUtils.getIpAddress(request);
        String ipSource = IpUtils.getIpSource(ipAddress);
        UserAgent userAgent = IpUtils.getUserAgent(request);

        // 封装用户详细信息
        return UserDetailsDTO.builder()
                .id(userAuthEntity.getId())
                .userInfoId(userInfoEntity.getId())
                .email(userInfoEntity.getEmail())
                .loginType(userAuthEntity.getLoginType())
                .username(userAuthEntity.getUsername())
                .password(userAuthEntity.getPassword())
                .roleList(roleLabelList)
                .nickname(userInfoEntity.getNickname())
                .avatar(userInfoEntity.getAvatar())
                .intro(userInfoEntity.getIntro())
                .webSite(userInfoEntity.getWebSite())
                .articleLikeSet(articleLikeSet)
                .commentLikeSet(commentLikeSet)
                .talkLikeSet(talkLikeSet)
                .ipAddress(ipAddress)
                .ipSource(ipSource)
                .isDisable(userInfoEntity.getIsDisable())
                .browser(userAgent.getBrowser().getName())
                .os(userAgent.getOperatingSystem().getName())
                .lastLoginTime(TimeUtils.now())
                .build();
    }

    @Autowired
    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Autowired
    public void setUserAuthMapper(UserAuthMapper userAuthMapper) {
        this.userAuthMapper = userAuthMapper;
    }

    @Autowired
    public void setUserInfoMapper(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

// TODO: 2022/12/25 抛出的异常可能需要 catch 逻辑

    @Autowired
    public void setRoleMapper(RoleMapper roleMapper) {
        UserDetailsServiceImpl.roleMapper = roleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("用户名不能为空！");
        }

        // 查询账号是否存在
        UserAuthEntity userAuthEntity = new LambdaQueryChainWrapper<>(userAuthMapper)
                .select(UserAuthEntity::getId, UserAuthEntity::getUserInfoId,
                        UserAuthEntity::getUsername, UserAuthEntity::getPassword,
                        UserAuthEntity::getLoginType)
                .eq(UserAuthEntity::getUsername, username).one();

        if (userAuthEntity == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        // TODO: 2022/12/25 可能需要异步执行

        return createUserDetails(userAuthEntity, httpServletRequest);
    }

}
