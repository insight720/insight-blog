package pers.project.blog.strategy;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.dto.userauth.SocialTokenDTO;
import pers.project.blog.dto.userauth.SocialUserInfoDTO;
import pers.project.blog.dto.userauth.UserDetailsDTO;
import pers.project.blog.entity.UserAuth;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.security.BlogUserDetails;
import pers.project.blog.service.UserAuthService;
import pers.project.blog.util.ConvertUtils;

import java.util.Optional;

/**
 * 抽象社交登录策略模板
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
public abstract class AbstractLoginStrategy implements LoginStrategy {

    private final UserAuthService userAuthService;

    public AbstractLoginStrategy(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public UserDetailsDTO login(String data) {
        // 获取第三方 Token 信息
        SocialTokenDTO socialToken;
        try {
            socialToken = getSocialToken(data);
            socialToken.setSocialUserInfo(getSocialUserInfo(socialToken));
        } catch (Exception cause) {
            throw new ServiceException("登录失败", cause);
        }
        // 获取用户认证信息
        UserAuth userAuth = userAuthService.getSocialUserAuth(socialToken);
        // 判断是否已注册，已注册则使用数据库信息，否则保存第三方用户信息到数据库并返回
        BlogUserDetails userDetails = Optional
                .ofNullable(userAuth)
                .map(userAuthService::getSocialUserDetails)
                .orElseGet(() -> userAuthService.createSocialUserDetails(socialToken));
        // 判断账号是否已被禁用或锁定（社交登录流程在事务中，这里抛出异常会回滚）
        if (!userDetails.isEnabled()) {
            throw new ServiceException("账号被禁用");
        } else if (!userDetails.isAccountNonLocked()) {
            throw new ServiceException("用户无可用权限角色");
        }
        // 将登录信息交给 Spring Security 管理
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken
                (userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 返回所需用户信息
        return ConvertUtils.convert(userDetails, UserDetailsDTO.class);
    }

    /**
     * 获取第三方 Token 信息
     *
     * @param data JSON 格式的认证数据
     * @return {@code  SocialTokenDTO} 第三方 Token 信息
     */
    public abstract SocialTokenDTO getSocialToken(String data) throws Exception;

    /**
     * 获取第三方用户信息
     *
     * @param socialToken 第三方 Token 信息
     * @return {@code  SocialUserInfoDTO} 第三方用户信息
     */
    public abstract SocialUserInfoDTO getSocialUserInfo(SocialTokenDTO socialToken) throws Exception;

}
