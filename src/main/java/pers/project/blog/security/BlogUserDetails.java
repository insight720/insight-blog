package pers.project.blog.security;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pers.project.blog.constant.GenericConst;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security 用户信息
 * <p>
 * 参见 <a href="https://springdoc.cn/spring-security/servlet/authentication/passwords/user-details.html">
 * UserDetails</a> 的文档。
 *
 * @author Luo Fei
 * @version 2022/12/23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogUserDetails implements UserDetails {

    // region UserAuth
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
     * 密码
     */
    private String password;
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
     * 浏览器
     */
    private String browser;
    /**
     * 操作系统
     */
    private String os;
    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;
    // endregion

    // region UserInfo
    /**
     * 邮箱号
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
    /**
     * 是否禁用（0 否 1 是）
     */
    private Integer isDisable;
    // endregion


    /**
     * 权限角色
     */
    private List<String> roleList;

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

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(roleList)) {
            return new ArrayList<>();
        }
        return roleList.stream()
                .sorted(Comparator.naturalOrder())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public boolean isAccountNonLocked() {
        // 权限角色列表为空，则用户账号锁定（一般是权限角色被禁用）
        return CollectionUtils.isNotEmpty(roleList);
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public boolean isEnabled() {
        return GenericConst.FALSE_OF_INT.equals(isDisable);
    }

}
