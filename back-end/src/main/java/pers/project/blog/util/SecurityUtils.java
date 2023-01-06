package pers.project.blog.util;

import org.springframework.security.core.context.SecurityContextHolder;
import pers.project.blog.dto.UserDetailsDTO;

/**
 * 安全相关的工具类
 *
 * @author Luo Fei
 * @date 2022/12/23
 */
public abstract class SecurityUtils {

    /**
     * 获取当前登录用户的信息
     *
     * @return {@link UserDetailsDTO} 提供核心用户信息
     */
    public static UserDetailsDTO getUserDetails() {
        // TODO: 2022/12/28 测试使用
        UserDetailsDTO principal = new UserDetailsDTO();
        principal.setUserInfoId(1);
        try {
            principal = (UserDetailsDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return principal;
    }

}
