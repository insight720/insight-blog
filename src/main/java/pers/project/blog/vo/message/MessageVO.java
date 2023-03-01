package pers.project.blog.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 留言数据
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 头像
     */
    @NotBlank(message = "头像不能为空")
    @Schema(description = "头像")
    private String avatar;

    /**
     * 留言内容
     */
    @NotBlank(message = "留言内容不能为空")
    @Schema(description = "留言内容")
    private String messageContent;

    /**
     * 弹幕速度
     */
    @NotNull(message = "弹幕速度不能为空")
    @Schema(description = "弹幕速度")
    private Integer time;

}
