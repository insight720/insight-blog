package pers.project.blog.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台文章列表搜索数据
 *
 * @author Luo Fei
 * @date 2023/2/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSearchVO {

    /**
     * 搜索内容
     */
    @Schema(description = "搜索内容")
    private String keywords;

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

    /**
     * 类型
     */
    @Schema(description = "类型")
    private Integer type;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 是否删除（0 否 1 是）
     */
    @Schema(description = "是否删除")
    private Integer isDelete;

}
