package pers.project.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * 用户认证信息
 *
 * @author Luo Fei
 * @date 2022-12-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_user_auth")
public class UserAuthEntity {

    /**
     * 用户账号 ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户信息 ID
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
     * 登录类型
     */
    private Integer loginType;

    /**
     * 用户登录 IP 地址
     */
    private String ipAddress;

    /**
     * IP 来源
     */
    private String ipSource;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;

}