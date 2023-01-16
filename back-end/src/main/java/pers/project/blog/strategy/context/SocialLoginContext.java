package pers.project.blog.strategy.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.enumeration.LoginTypeEnum;
import pers.project.blog.dto.UserInfoDTO;
import pers.project.blog.strategy.SocialLoginStrategy;

import java.util.Map;

/**
 * 社交登录上下文
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Component
public final class SocialLoginContext {

    private static Map<String, SocialLoginStrategy> socialLoginStrategyMap;

    private SocialLoginContext() {
    }

    /**
     * 执行第三方登录策略
     *
     * @param data          数据
     * @param loginTypeEnum 登录枚举类型
     * @return {@code  UserInfoDTO} 用户信息
     */
    public static UserInfoDTO executeStrategy(String data, LoginTypeEnum loginTypeEnum) {
        return socialLoginStrategyMap.get(loginTypeEnum.getStrategy()).login(data);
    }

    @Autowired
    public void setSearchStrategyMap(Map<String, SocialLoginStrategy> socialLoginStrategyMap) {
        SocialLoginContext.socialLoginStrategyMap = socialLoginStrategyMap;
    }

}
