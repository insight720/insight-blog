package pers.project.blog.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 评论数据
 *
 * @author Luo Fei
 * @version 2023/1/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {

    /**
     * 回复用户 ID
     */
    @Schema(description = "回复用户 ID")
    private Integer replyUserId;

    /**
     * 评论主题 ID
     */
    @Schema(description = "主题 ID")
    private Integer topicId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Schema(description = "评论内容")
    private String commentContent;

    /**
     * 父评论 ID
     */
    @Schema(description = "评论父 ID")
    private Integer parentId;

    /**
     * 类型
     */
    @NotNull(message = "评论类型不能为空")
    @Schema(description = "评论类型")
    private Integer type;

}
