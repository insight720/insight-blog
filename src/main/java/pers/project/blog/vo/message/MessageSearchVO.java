package pers.project.blog.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 留言搜索数据
 *
 * @author Luo Fei
 * @version 2023/2/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSearchVO {

    /**
     * 关键词
     */
    @Schema(description = "搜索内容")
    private String keywords;

    /**
     * 审核状态（0 否 1 是）
     */
    @Schema(description = "是否审核")
    private Integer isReview;

}
