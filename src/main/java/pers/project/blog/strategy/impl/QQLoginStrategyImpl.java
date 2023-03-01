package pers.project.blog.strategy.impl;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pers.project.blog.dto.userauth.QQTokenDTO;
import pers.project.blog.dto.userauth.QQUserInfoDTO;
import pers.project.blog.dto.userauth.SocialTokenDTO;
import pers.project.blog.dto.userauth.SocialUserInfoDTO;
import pers.project.blog.enums.LoginTypeEnum;
import pers.project.blog.property.QQProperties;
import pers.project.blog.service.UserAuthService;
import pers.project.blog.strategy.AbstractLoginStrategy;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.util.StrRegexUtils;
import pers.project.blog.util.WebUtils;
import pers.project.blog.vo.userauth.QQLoginVO;

import java.util.Collections;
import java.util.Map;

import static pers.project.blog.constant.GenericConst.THREE;
import static pers.project.blog.constant.LoginConst.*;


/**
 * QQ 登录策略
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Component
@EnableConfigurationProperties(QQProperties.class)
public class QQLoginStrategyImpl extends AbstractLoginStrategy {

    private final QQProperties qqProperties;

    public QQLoginStrategyImpl(UserAuthService userAuthService,
                               QQProperties qqProperties) {
        super(userAuthService);
        this.qqProperties = qqProperties;
    }

    @Override
    public SocialTokenDTO getSocialToken(String data) {
        if (StrRegexUtils.isBlank(data)) {
            throw new RuntimeException("JSON 微博认证数据为空");
        }
        QQLoginVO qqLoginVO = ConvertUtils.parseJson(data, QQLoginVO.class);
        // 校验 QQ Token 信息
        checkQQToken(qqLoginVO);
        // 返回 Token 信息
        return SocialTokenDTO.builder()
                .openId(qqLoginVO.getOpenId())
                .accessToken(qqLoginVO.getAccessToken())
                .loginType(LoginTypeEnum.QQ.getType())
                .build();
    }

    @Override
    public SocialUserInfoDTO getSocialUserInfo(SocialTokenDTO socialToken) {
        // 定义请求参数
        Map<String, String> parameterMap = CollectionUtils.newHashMap(THREE);
        parameterMap.put(QQ_OPEN_ID, socialToken.getOpenId());
        parameterMap.put(ACCESS_TOKEN, socialToken.getAccessToken());
        parameterMap.put(OAUTH_CONSUMER_KEY, qqProperties.getAppId());
        // 获取 QQ 返回的用户信息
        String json = WebUtils.get(qqProperties.getUserInfoUrl(), String.class, parameterMap);
        if (StrRegexUtils.isBlank(json)) {
            throw new RuntimeException("获取 QQ 用户信息异常");
        }
        QQUserInfoDTO qqUserInfoDTO = ConvertUtils.parseJson(json, QQUserInfoDTO.class);
        return SocialUserInfoDTO.builder()
                .nickname(qqUserInfoDTO.getNickname())
                .avatar(qqUserInfoDTO.getAvatar())
                .build();
    }

    /**
     * 校验 QQ Token 信息
     *
     * @param qqLoginVO QQ 登录信息
     */
    private void checkQQToken(QQLoginVO qqLoginVO) {
        // 根据 Token 获取 QQ openId 信息
        Map<String, String> accessToken = Collections.singletonMap
                (ACCESS_TOKEN, qqLoginVO.getAccessToken());
        String result = WebUtils.get(qqProperties.getCheckTokenUrl(), String.class, accessToken);
        if (StrRegexUtils.isBlank(result)) {
            throw new RuntimeException("获取 QQ openId 异常");
        }
        // 获取括号内内容
        String json = StrRegexUtils.getBracketsContent(result);
        QQTokenDTO qqTokenDTO = ConvertUtils.parseJson(json, QQTokenDTO.class);
        // 判断 openId 是否一致
        if (!qqLoginVO.getOpenId().equals(qqTokenDTO.getOpenid())) {
            throw new RuntimeException("QQ openId 不一致");
        }
    }

}
