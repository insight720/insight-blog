package pers.project.blog.strategy;

import pers.project.blog.dto.userauth.UserDetailsDTO;

/**
 * 社交登录策略
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
public interface LoginStrategy {

    /**
     * 登录
     *
     * @param data JSON 格式的认证数据
     * @return {@code UserInfoDTO} 用户信息
     */
    UserDetailsDTO login(String data);

}
