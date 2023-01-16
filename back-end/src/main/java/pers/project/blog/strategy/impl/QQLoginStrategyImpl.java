package pers.project.blog.strategy.impl;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import pers.project.blog.configuration.property.QQProperties;
import pers.project.blog.constant.SocialLoginConstant;
import pers.project.blog.constant.enumeration.LoginTypeEnum;
import pers.project.blog.constant.enumeration.ResultStatusEnum;
import pers.project.blog.dto.QQTokenDTO;
import pers.project.blog.dto.QQUserInfoDTO;
import pers.project.blog.dto.SocialTokenDTO;
import pers.project.blog.dto.SocialUserInfoDTO;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.service.UserAuthService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.SecurityUtils;
import pers.project.blog.vo.QQLoginVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Luo Fei
 * @date 2023/1/15
 */
@Component
@EnableConfigurationProperties(QQProperties.class)
public class QQLoginStrategyImpl extends AbstractSocialLoginStrategy {

    private final QQProperties qqProperties;

    public QQLoginStrategyImpl(HttpServletRequest httpServletRequest,
                               UserAuthService userAuthService,
                               QQProperties qqProperties) {
        super(httpServletRequest, userAuthService);
        this.qqProperties = qqProperties;
    }

    @Override
    public SocialUserInfoDTO getSocialUserInfo(SocialTokenDTO socialToken) {
        // 定义请求参数
        HashMap<Object, Object> parameterMap = CollectionUtils.newHashMap(3);
        parameterMap.put(SocialLoginConstant.QQ_OPEN_ID, socialToken.getOpenId());
        parameterMap.put(SocialLoginConstant.ACCESS_TOKEN, socialToken.getAccessToken());
        parameterMap.put(SocialLoginConstant.OAUTH_CONSUMER_KEY, qqProperties.getAppId());

        // TODO: 2023/1/15 封装
        // 获取QQ返回的用户信息
        RestTemplate restTemplate = new RestTemplate();
        String jsonString = restTemplate.getForObject(qqProperties.getUserInfoUrl(), String.class, parameterMap);
        QQUserInfoDTO qqUserInfoDTO = ConversionUtils.parseJson(jsonString, QQUserInfoDTO.class);

        return SocialUserInfoDTO.builder()
                .nickname(Objects.requireNonNull
                        (qqUserInfoDTO, "未知 null").getNickname())
                .avatar(qqUserInfoDTO.getFigureurl_qq_1())
                .build();
    }

    @Override
    public SocialTokenDTO getSocialToken(String data) {
        QQLoginVO qqLoginVO = ConversionUtils.parseJson(data, QQLoginVO.class);

        // 校验 QQ Token 信息
        checkQQToken(qqLoginVO);

        // 返回 Token信息
        return SocialTokenDTO.builder()
                .openId(qqLoginVO.getOpenId())
                .accessToken(qqLoginVO.getAccessToken())
                .loginType(LoginTypeEnum.QQ.getType())
                .build();
    }

    /**
     * 校验 QQ Token 信息
     *
     * @param qqLoginVO QQ 登录信息
     */
    private void checkQQToken(QQLoginVO qqLoginVO) {
        // TODO: 2023/1/15 优化一下
        // 根据 Token 获取 QQ openId 信息
        Map<String, String> qqData = Collections.singletonMap
                (SocialLoginConstant.ACCESS_TOKEN, qqLoginVO.getAccessToken());
        // TODO: 2023/1/15 可以封装 RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(qqProperties.getCheckTokenUrl(), String.class, qqData);
        QQTokenDTO qqTokenDTO = ConversionUtils.parseJson(SecurityUtils.getBracketsContent
                (Objects.requireNonNull(result, "未知 null")), QQTokenDTO.class);

        // 判断openId是否一致
        if (!qqLoginVO.getOpenId().equals(qqTokenDTO.getOpenid())) {
            throw new ServiceException(ResultStatusEnum.QQ_LOGIN_ERROR.getDescription());
        }
    }

}
