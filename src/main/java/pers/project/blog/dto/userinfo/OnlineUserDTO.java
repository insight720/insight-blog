package pers.project.blog.dto.userinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 在线用户数据
 *
 * @author Luo Fei
 * @version 2023/1/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlineUserDTO {

    /**
     * 用户信息 ID
     */
    private Integer userInfoId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户 IP
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
     * 最近登录时间
     */
    private LocalDateTime lastLoginTime;

}
