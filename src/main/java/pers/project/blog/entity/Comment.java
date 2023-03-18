package pers.project.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论
 *
 * @author Luo Fei
 * @version 2023-01-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_comment")
public class Comment {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 评论用户 ID
     */
    private Integer userId;

    /**
     * 评论主题 ID
     */
    private Integer topicId;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 回复用户 ID
     */
    private Integer replyUserId;

    /**
     * 父评论 ID
     */
    private Integer parentId;

    /**
     * 评论类型 1.文章 2.友链 3.说说
     */
    private Integer type;

    /**
     * 是否删除  0 否 1 是
     */
    private Integer isDelete;

    /**
     * 是否审核
     */
    private Integer isReview;

    /**
     * 评论时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}