package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章数据
 *
 * @author Luo Fei
 * @date 2023/1/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "文章数据")
public class ArticleVO {

    /**
     * 文章 ID
     */
    @Schema(name = "id", title = "文章 ID", type = "Integer")
    private Integer id;

    /**
     * 标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Schema(name = "articleTitle", title = "文章标题", type = "String")
    private String articleTitle;

    /**
     * 内容
     */
    @NotBlank(message = "文章内容不能为空")
    @Schema(name = "articleContent", title = "文章内容", type = "String")
    private String articleContent;

    /**
     * 文章封面
     */
    @Schema(name = "articleCover", title = "文章缩略图", type = "String")
    private String articleCover;

    /**
     * 文章分类
     */
    @Schema(name = "category", title = "文章分类", type = "Integer")
    private String categoryName;

    /**
     * 文章标签
     */
    @Schema(name = "tagNameList", title = "文章标签", type = "List<Integer>")
    private List<String> tagNameList;

    /**
     * 文章类型
     */
    @Schema(name = "type", title = "文章类型", type = "Integer")
    private Integer type;

    /**
     * 原文链接
     */
    @Schema(name = "originalUrl", title = "原文链接", type = "String")
    private String originalUrl;

    /**
     * 是否置顶
     */
    @Schema(name = "isTop", title = "是否置顶", type = "Integer")
    private Integer isTop;

    /**
     * 文章状态 1.公开 2.私密 3.评论可见
     */
    @NotNull
    @Schema(name = "status", title = "文章状态", type = "String")
    private Integer status;
    // TODO: 2023/1/8 Hexo 文章添加了 createTime 一个属性
    @Nullable
    @Schema(name = "createTime", title = "创建时间", type = "LocalDateTime")
    private LocalDateTime createTime;

}
