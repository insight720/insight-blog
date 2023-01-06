package pers.project.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 分类
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_category")
public class CategoryEntity {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 分类名
     */
    private String categoryName;

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