package pers.project.blog.strategy.impl;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pers.project.blog.configuration.property.WeiboProperties;
import pers.project.blog.constant.enumeration.LoginTypeEnum;
import pers.project.blog.dto.SocialTokenDTO;
import pers.project.blog.dto.SocialUserInfoDTO;
import pers.project.blog.dto.WeiboTokenDTO;
import pers.project.blog.dto.WeiboUserInfoDTO;
import pers.project.blog.service.UserAuthService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.vo.WeiboLoginVO;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static pers.project.blog.constant.SocialLoginConstant.*;

/**
 * @author Luo Fei
 * @date 2023/1/15
 */
@Component
@EnableConfigurationProperties(WeiboProperties.class)
public class WeiboLoginStrategyImpl extends AbstractSocialLoginStrategy {

    private final WeiboProperties weiboProperties;

    public WeiboLoginStrategyImpl(HttpServletRequest httpServletRequest,
                                  UserAuthService userAuthService,
                                  WeiboProperties weiboProperties) {
        super(httpServletRequest, userAuthService);
        this.weiboProperties = weiboProperties;
    }

    @Override
    public SocialUserInfoDTO getSocialUserInfo(SocialTokenDTO socialToken) {
        // 定义请求参数
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put(UID, socialToken.getOpenId());
        parameterMap.put(ACCESS_TOKEN, socialToken.getAccessToken());

        // 获取微博用户信息
        WeiboUserInfoDTO weiboUserInfoDTO = new RestTemplate()
                .getForObject(weiboProperties.getRedirectUrl(), WeiboUserInfoDTO.class, parameterMap);

        // 返回用户信息
        return SocialUserInfoDTO.builder()
                .nickname(Objects.requireNonNull(weiboUserInfoDTO).getScreen_name())
                .avatar(weiboUserInfoDTO.getAvatar_hd())
                .build();
    }

    @Override
    public SocialTokenDTO getSocialToken(String data) {
        WeiboLoginVO weiboLoginVO = ConversionUtils.parseJson(data, WeiboLoginVO.class);

        // 获取微博 Token 信息
        WeiboTokenDTO weiboTokenDTO = getWeiboToken(weiboLoginVO);

        // 返回token信息
        return SocialTokenDTO.builder()
                .openId(weiboTokenDTO.getUid())
                .accessToken(weiboTokenDTO.getAccess_token())
                .loginType(LoginTypeEnum.WEIBO.getType())
                .build();
    }

    /**
     * 获取微博token信息
     *
     * @param weiboLoginVO 微博登录信息
     * @return {@code  WeiboTokenDTO} 微博token
     */
    private WeiboTokenDTO getWeiboToken(WeiboLoginVO weiboLoginVO) {
        // 根据code换取微博uid和accessToken
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put(CLIENT_SECRET, weiboProperties.getAppSecret());
        parameterMap.put(GRANT_TYPE, weiboProperties.getGrantType());
        parameterMap.put(REDIRECT_URI, weiboProperties.getRedirectUrl());
        parameterMap.put(CODE, weiboLoginVO.getCode());
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(parameterMap, null);
        return new RestTemplate()
                .exchange(weiboProperties.getAccessTokenUrl(),
                        HttpMethod.POST, httpEntity, WeiboTokenDTO.class)
                .getBody();
    }

}
