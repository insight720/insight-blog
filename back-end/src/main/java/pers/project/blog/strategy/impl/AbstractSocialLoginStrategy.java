package pers.project.blog.strategy.impl;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import pers.project.blog.constant.BooleanConstant;
import pers.project.blog.dto.SocialTokenDTO;
import pers.project.blog.dto.SocialUserInfoDTO;
import pers.project.blog.dto.UserDetailsDTO;
import pers.project.blog.dto.UserInfoDTO;
import pers.project.blog.entity.UserAuthEntity;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.service.UserAuthService;
import pers.project.blog.strategy.SocialLoginStrategy;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.IpUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 抽象登录策略模板
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
public abstract class AbstractSocialLoginStrategy implements SocialLoginStrategy {

    private final HttpServletRequest httpServletRequest;

    private final UserAuthService userAuthService;

    public AbstractSocialLoginStrategy(HttpServletRequest httpServletRequest,
                                       UserAuthService userAuthService) {
        this.httpServletRequest = httpServletRequest;
        this.userAuthService = userAuthService;
    }

    @Override
    public UserInfoDTO login(String data) {
        // 获取第三方 Token 信息
        SocialTokenDTO socialToken = getSocialToken(data);
        socialToken.setSocialUserInfo(getSocialUserInfo(socialToken));

        // TODO: 2023/1/15 可以封装 httpServletRequest
        // 获取用户 IP 信息
        String ipAddress = IpUtils.getIpAddress(httpServletRequest);
        String ipSource = IpUtils.getIpSource(ipAddress);

        // 获取用户信息
        UserAuthEntity userAuthEntity = userAuthService.getUserAuth(socialToken);

        // 判断是否已注册，已注册则获取数据库信息，否则保存第三方用户信息到数据库并返回
        UserDetailsDTO userDetailsDTO = Optional
                .ofNullable(userAuthEntity)
                .map(userAuth -> userAuthService.getUserDetails(userAuth, ipAddress, ipSource))
                .orElseGet(() -> userAuthService.saveUserDetails(socialToken, ipAddress, ipSource));

        // 判断账号是否已被禁用
        if (userDetailsDTO.getIsDisable().equals(BooleanConstant.TRUE)) {
            throw new ServiceException("账号被禁用");
        }

        // 将登录信息交给 Spring Security 管理
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken
                (userDetailsDTO, null, userDetailsDTO.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 返回用户信息
        return ConversionUtils.convertObject(userDetailsDTO, UserInfoDTO.class);
    }

    /**
     * 获取第三方用户信息
     *
     * @param socialToken 第三方 Token 信息
     * @return {@code  SocialUserInfoDTO} 第三方用户信息
     */
    public abstract SocialUserInfoDTO getSocialUserInfo(SocialTokenDTO socialToken);

    /**
     * 获取第三方 Token 信息
     *
     * @param data 数据
     * @return {@code  SocialTokenDTO} 第三方 Token 信息
     */
    public abstract SocialTokenDTO getSocialToken(String data);

}
