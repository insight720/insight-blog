package pers.project.blog.vo;

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
 * @date 2023/1/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "评论信息")
public class CommentVO {

    /**
     * 回复用户 ID
     */
    @Schema(name = "replyUserId", title = "回复用户 ID", type = "Integer")
    private Integer replyUserId;

    /**
     * 评论主题 ID
     */
    @Schema(name = "topicId", title = "主题 ID", type = "Integer")
    private Integer topicId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Schema(name = "commentContent", title = "评论内容", type = "String")
    private String commentContent;

    /**
     * 父评论 ID
     */
    @Schema(name = "parentId", title = "评论父 ID", type = "Integer")
    private Integer parentId;

    /**
     * 类型
     */
    @NotNull(message = "评论类型不能为空")
    @Schema(name = "type", title = "评论类型", type = "Integer")
    private Integer type;

}
