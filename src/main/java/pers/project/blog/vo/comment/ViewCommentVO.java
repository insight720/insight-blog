package pers.project.blog.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 前台查看评论所需数据
 *
 * @author Luo Fei
 * @date 2023/2/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewCommentVO {

    /**
     * 评论主题 ID
     */
    @Schema(description = "主题 ID")
    private Integer topicId;

    /**
     * 类型
     */
    @NotNull(message = "评论类型不能为 null")
    @Schema(description = "评论类型")
    private Integer type;

}
