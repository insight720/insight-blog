package pers.project.blog.strategy.impl;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pers.project.blog.dto.userauth.SocialTokenDTO;
import pers.project.blog.dto.userauth.SocialUserInfoDTO;
import pers.project.blog.dto.userauth.WeiboTokenDTO;
import pers.project.blog.dto.userauth.WeiboUserInfoDTO;
import pers.project.blog.enums.LoginTypeEnum;
import pers.project.blog.property.WeiboProperties;
import pers.project.blog.service.UserAuthService;
import pers.project.blog.strategy.AbstractLoginStrategy;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.util.StrRegexUtils;
import pers.project.blog.util.WebUtils;
import pers.project.blog.vo.userauth.WeiboLoginVO;

import java.util.Map;

import static pers.project.blog.constant.GenericConst.FIVE;
import static pers.project.blog.constant.GenericConst.TWO;
import static pers.project.blog.constant.LoginConst.*;

/**
 * 微博登录策略
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Component
@EnableConfigurationProperties(WeiboProperties.class)
public class WeiboLoginStrategyImpl extends AbstractLoginStrategy {

    private final WeiboProperties weiboProperties;

    public WeiboLoginStrategyImpl(UserAuthService userAuthService,
                                  WeiboProperties weiboProperties) {
        super(userAuthService);
        this.weiboProperties = weiboProperties;
    }

    @Override
    public SocialTokenDTO getSocialToken(String data) {
        if (StrRegexUtils.isBlank(data)) {
            throw new RuntimeException("JSON 微博认证数据为空");
        }
        WeiboLoginVO weiboLoginVO = ConvertUtils.parseJson(data, WeiboLoginVO.class);
        // 获取微博 Token 信息
        WeiboTokenDTO weiboTokenDTO = getWeiboToken(weiboLoginVO);
        return SocialTokenDTO.builder()
                .openId(weiboTokenDTO.getUid())
                .accessToken(weiboTokenDTO.getAccess_token())
                .loginType(LoginTypeEnum.WEIBO.getType())
                .build();
    }

    @Override
    public SocialUserInfoDTO getSocialUserInfo(SocialTokenDTO socialToken) {
        Map<String, String> parameterMap = CollectionUtils.newHashMap(TWO);
        parameterMap.put(UID, socialToken.getOpenId());
        parameterMap.put(ACCESS_TOKEN, socialToken.getAccessToken());
        // 获取微博用户信息
        String json = WebUtils.get
                (weiboProperties.getUserInfoUrl(), String.class, parameterMap);
        if (StrRegexUtils.isBlank(json)) {
            throw new RuntimeException("微博用户信息为空");
        }
        WeiboUserInfoDTO weiboUserInfoDTO
                = ConvertUtils.parseJson(json, WeiboUserInfoDTO.class);
        return SocialUserInfoDTO.builder()
                .nickname(weiboUserInfoDTO.getNickname())
                .avatar(weiboUserInfoDTO.getAvatar())
                .build();
    }

    /**
     * 获取微博 Token 信息
     *
     * @param weiboLoginVO 微博登录信息
     * @return {@code  WeiboTokenDTO} 微博 Token
     */
    private WeiboTokenDTO getWeiboToken(WeiboLoginVO weiboLoginVO) {
        Map<String, String> parameterMap = CollectionUtils.newHashMap(FIVE);
        parameterMap.put(CLIENT_ID, weiboProperties.getAppId());
        parameterMap.put(CLIENT_SECRET, weiboProperties.getAppSecret());
        parameterMap.put(GRANT_TYPE, weiboProperties.getGrantType());
        parameterMap.put(REDIRECT_URI, weiboProperties.getRedirectUrl());
        parameterMap.put(CODE, weiboLoginVO.getCode());
        ResponseEntity<String> responseEntity;
        try {
            // 用 code 获取微博 uid 和 access_token
            responseEntity = WebUtils.exchange
                    (weiboProperties.getAccessTokenUrl(),
                            HttpMethod.POST, null, String.class, parameterMap);
        } catch (Exception cause) {
            throw new RuntimeException("获取微博 Token 信息异常", cause);
        }
        String json = responseEntity.getBody();
        if (StrRegexUtils.isBlank(json)) {
            throw new RuntimeException("微博 Token 信息为空");
        }
        return ConvertUtils.parseJson(json, WeiboTokenDTO.class);
    }

}
