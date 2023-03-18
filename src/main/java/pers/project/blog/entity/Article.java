package pers.project.blog.entity;

import com.alibaba.fastjson2.annotation.JSONField;
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
 * @version 2022-12-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_article")
public class Article {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 作者
     */
    @JSONField(alternateNames = {"userId", "user_id"})
    private Integer userId;

    /**
     * 文章分类
     */
    @JSONField(alternateNames = {"categoryId", "category_id"})
    private Integer categoryId;

    /**
     * 文章缩略图
     */
    @JSONField(alternateNames = {"articleCover", "article_cover"})
    private String articleCover;

    /**
     * 标题
     */
    @JSONField(alternateNames = {"articleTitle", "article_title"})
    private String articleTitle;

    /**
     * 内容
     */
    @JSONField(alternateNames = {"articleContent", "article_content"})
    private String articleContent;

    /**
     * 文章类型（1 原创 2 转载 3 翻译）
     */
    private Integer type;

    /**
     * 原文链接
     */
    @JSONField(alternateNames = {"originalUrl", "original_url"})
    private String originalUrl;

    /**
     * 是否置顶 0 否 1是
     */
    @JSONField(alternateNames = {"isTop", "is_top"})
    private Integer isTop;

    /**
     * 是否删除  0 否 1 是
     */
    @JSONField(alternateNames = {"isDelete", "is_delete"})
    private Integer isDelete;

    /**
     * 状态值 1 公开 2 私密 3 评论可见
     */
    private Integer status;

    /**
     * 发表时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JSONField(alternateNames = {"createTime", "create_time"})
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JSONField(alternateNames = {"updateTime", "update_time"})
    private LocalDateTime updateTime;

}