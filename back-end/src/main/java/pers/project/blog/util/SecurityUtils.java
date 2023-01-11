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

    // TODO: 2023/1/6 工具类只有一个方法

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

    /**
     * 删除标签
     *
     * @param source 文本
     * @return 过滤后的文本
     */
    public static String deleteHMTLTag(String source) {
        // 删除转义字符
        source = source.replaceAll("&.{2,6}?;", "");
        // 删除script标签
        source = source.replaceAll("<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>", "");
        // 删除style标签
        source = source.replaceAll("<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>", "");
        return source;
    }

}
