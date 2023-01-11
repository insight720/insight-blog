package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import pers.project.blog.constant.BooleanConstant;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 对 {@link UserDetailsService} 检索到的核心用户信息建模
 *
 * @author Luo Fei
 * @date 2022/12/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO implements UserDetails {
    // TODO: 2023/1/7  两个 ID 傻傻分不清
    /**
     * 用户账号 ID
     */
    private Integer id;

    /**
     * 用户信息 ID
     */
    private Integer userInfoId;

    /**
     * 邮箱号
     */
    private String email;

    /**
     * 登录方式
     */
    private Integer loginType;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户角色
     */
    private List<String> roleList;

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

    /**
     * 点赞文章集合
     */
    private Set<Object> articleLikeSet;

    /**
     * 点赞评论集合
     */
    private Set<Object> commentLikeSet;

    /**
     * 点赞说说集合
     */
    private Set<Object> talkLikeSet;

    /**
     * 用户登录 IP 地址
     */
    private String ipAddress;

    /**
     * IP 地址来源
     */
    private String ipSource;


    /**
     * 是否禁用（0 不禁用 1禁用）
     */
    private Integer isDisable;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 最近登录时间
     */
    private LocalDateTime lastLoginTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleList.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(SimpleGrantedAuthority::getAuthority))));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return BooleanConstant.FALSE.equals(isDisable);
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
