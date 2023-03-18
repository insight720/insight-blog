package pers.project.blog.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类或标签文章预览数据
 *
 * @author Luo Fei
 * @version 2023/2/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticlePreviewVO {

    /**
     * 分类 ID
     */
    @Schema(description = "分类 ID")
    private Integer categoryId;

    /**
     * 标签 ID
     */
    @Schema(description = "标签 ID")
    private Integer tagId;

}
