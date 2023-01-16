package pers.project.blog.strategy;

import pers.project.blog.dto.UserInfoDTO;

/**
 * 社交登录策略
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
public interface SocialLoginStrategy {

    /**
     * 登录
     *
     * @param data JSON 登录数据
     * @return {@code UserInfoDTO} 用户信息
     */
    UserInfoDTO login(String data);

}
