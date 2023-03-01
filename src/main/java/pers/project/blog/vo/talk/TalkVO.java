package pers.project.blog.vo.talk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 说说数据
 *
 * @author Luo Fei
 * @date 2023/1/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TalkVO {

    /**
     * 说说 ID
     */
    @Schema(description = "说说 ID")
    private Integer id;

    /**
     * 说说内容
     */
    @Schema(description = "说说内容")
    @NotBlank(message = "说说内容不能为空")
    private String content;

    /**
     * 图片
     */
    @Schema(description = "说说图片")
    private String images;

    /**
     * 是否置顶
     */
    @Schema(description = "置顶状态")
    @NotNull(message = "置顶状态不能为空")
    private Integer isTop;

    /**
     * 说说状态 1.公开 2.私密
     */
    @Schema(description = "说说状态")
    @NotNull(message = "说说状态不能为空")
    private Integer status;

}
