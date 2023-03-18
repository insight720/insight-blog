package pers.project.blog.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论搜索数据
 *
 * @author Luo Fei
 * @version 2023/2/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentSearchVO {

    /**
     * 关键词
     */
    @Schema(description = "搜索内容")
    private String keywords;

    /**
     * 类型
     */
    @Schema(description = "类型")
    private Integer type;

    /**
     * 是否审核
     */
    @Schema(description = "是否审核")
    private Integer isReview;

}
