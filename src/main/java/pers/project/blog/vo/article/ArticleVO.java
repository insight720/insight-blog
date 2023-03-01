package pers.project.blog.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class ArticleVO {

    /**
     * 文章 ID
     */
    @Schema(description = "文章 ID")
    private Integer id;

    /**
     * 标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Schema(description = "文章标题")
    private String articleTitle;

    /**
     * 内容
     */
    @NotBlank(message = "文章内容不能为空")
    @Schema(description = "文章内容")
    private String articleContent;

    /**
     * 文章封面
     */
    @Schema(description = "文章文章封面")
    private String articleCover;

    /**
     * 文章分类
     */
    @Schema(description = "文章分类")
    private String categoryName;

    /**
     * 文章标签
     */
    @Schema(description = "文章标签")
    private List<String> tagNameList;

    /**
     * 文章类型
     */
    @Schema(description = "文章类型")
    private Integer type;

    /**
     * 原文链接
     */
    @Schema(description = "原文链接")
    private String originalUrl;

    /**
     * 是否置顶
     */
    @Schema(description = "是否置顶")
    private Integer isTop;

    /**
     * 文章状态 1.公开 2.私密 3.评论可见
     */
    @NotNull(message = "文章状态不能为空")
    @Schema(description = "文章状态")
    private Integer status;

    /**
     * 创建时间（仅 Hexo 文章）
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
