package pers.project.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 菜单
 *
 * @author Luo Fei
 * @date 2022/12/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_menu")
public class Menu {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 菜单名
     */
    private String name;

    /**
     * 菜单路径
     */
    private String path;

    /**
     * 组件
     */
    private String component;

    /**
     * 菜单 icon
     */
    private String icon;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 父 ID
     */
    private Integer parentId;

    /**
     * 是否隐藏（0 否 1 是）
     */
    private Integer isHidden;

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

}