package pers.project.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_article")
public class ArticleEntity {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 作者
     */
    private Integer userId;

    /**
     * 文章分类
     */
    private Integer categoryId;

    /**
     * 文章缩略图
     */
    private String articleCover;

    /**
     * 标题
     */
    private String articleTitle;

    /**
     * 内容
     */
    private String articleContent;

    /**
     * 文章类型（1 原创 2 转载 3 翻译）
     */
    private Integer type;

    /**
     * 原文链接
     */
    private String originalUrl;

    /**
     * 是否置顶 0 否 1是
     */
    private Integer isTop;

    /**
     * 是否删除  0 否 1 是
     */
    private Integer isDelete;

    /**
     * 状态值 1 公开 2 私密 3 评论可见
     */
    private Integer status;

    /**
     * 发表时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}