package pers.project.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 后台用户
 *
 * @author Luo Fei
 * @date 2022/12/31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {

    /**
     * ID
     */
    private Integer id;

    /**
     * 用户信息 ID
     */
    private Integer userInfoId;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户角色
     */
    @Schema
    private List<UserRoleDTO> roleList;

    /**
     * 登录类型
     */
    private Integer loginType;

    /**
     * 用户登录 IO 地址
     */
    private String ipAddress;

    /**
     * IP 来源
     */
    private String ipSource;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最近登录时间
     */
    private Date lastLoginTime;

    /**
     * 用户评论状态
     */
    private Integer isDisable;

    // TODO: 2022/12/31 这属性可能没用过
    /**
     * 状态
     */
    private Integer status;

}
