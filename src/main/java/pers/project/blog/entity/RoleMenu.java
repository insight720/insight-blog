package pers.project.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色 ID 与 菜单 ID 的映射
 *
 * @author Luo Fei
 * @version 2023-01-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_role_menu")
public class RoleMenu {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 角色 ID
     */
    private Integer roleId;

    /**
     * 菜单 ID
     */
    private Integer menuId;

}