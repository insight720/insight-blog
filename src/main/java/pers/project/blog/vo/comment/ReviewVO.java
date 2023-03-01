package pers.project.blog.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 评论和留言审核信息
 *
 * @author Luo Fei
 * @date 2023/1/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewVO {

    /**
     * ID 列表
     */
    @NotEmpty(message = "ID 不能为空")
    @Schema(description = "ID 列表")
    private List<Integer> idList;

    /**
     * 审核状态
     */
    @NotNull(message = "审核状态不能为空")
    @Schema(description = "删除状态")
    private Integer isReview;

}
