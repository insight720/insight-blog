package pers.project.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 说说
 *
 * @author Luo Fei
 * @version 2023-01-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_talk")
public class Talk {

    /**
     * 说说 ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户 ID
     */
    private Integer userId;

    /**
     * 说说内容
     */
    private String content;

    /**
     * 图片 URL（JSON 数组）
     */
    private String images;

    /**
     * 是否置顶（0 否 1 是）
     */
    private Integer isTop;

    /**
     * 状态（1 公开 2 私密）
     */
    private Integer status;

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