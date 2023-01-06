package pers.project.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户与角色的 ID 映射
 *
 * @author Luo Fei
 * @date 2023-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_user_role")
public class UserRoleEntity {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户 ID
     */
    private Integer userId;

    /**
     * 角色 ID
     */
    private Integer roleId;

}