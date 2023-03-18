package pers.project.blog.dto.userauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Spring Security 用户的部分信息
 *
 * @author Luo Fei
 * @version 2022/12/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {

    // region BlogUserDetails without password, isDisable, browser, os, roleList

    // region UserAuth without password
    /**
     * 用户认证 ID
     */
    private Integer id;
    /**
     * 用户账号信息 ID
     */
    private Integer userInfoId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 登录方式
     */
    private Integer loginType;
    /**
     * IP 地址
     */
    private String ipAddress;
    /**
     * IP 来源
     */
    private String ipSource;
    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;
    // endregion

    // region UserInfo without isDisable
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 用户简介
     */
    private String intro;
    /**
     * 个人网站
     */
    private String webSite;
    // endregion

    /**
     * 点赞文章 ID 集合
     */
    private Set<Object> articleLikeSet;
    /**
     * 点赞评论 ID 集合
     */
    private Set<Object> commentLikeSet;
    /**
     * 点赞说说 ID 集合
     */
    private Set<Object> talkLikeSet;

    // endregion

}
