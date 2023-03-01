package pers.project.blog.strategy.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.userauth.UserDetailsDTO;
import pers.project.blog.enums.LoginTypeEnum;
import pers.project.blog.strategy.LoginStrategy;

import java.util.Map;

/**
 * 社交登录上下文
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Component
public final class LoginContext {

    private static Map<String, LoginStrategy> socialLoginStrategyMap;

    @Autowired
    public void setSocialLoginStrategyMap(Map<String, LoginStrategy> socialLoginStrategyMap) {
        LoginContext.socialLoginStrategyMap = socialLoginStrategyMap;
    }

    private LoginContext() {
    }

    /**
     * 执行第三方登录策略
     *
     * @param data          JSON 格式的认证数据
     * @param loginTypeEnum 登录枚举类型
     * @return {@code  UserInfoDTO} 用户信息
     */
    public static UserDetailsDTO executeStrategy(String data, LoginTypeEnum loginTypeEnum) {
        return socialLoginStrategyMap.get(loginTypeEnum.getStrategy()).login(data);
    }

}
