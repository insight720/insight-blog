package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 说说信息
 *
 * @author Luo Fei
 * @date 2023/1/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "说说信息")
public class TalkVO {

    /**
     * 说说 ID
     */
    @Schema(name = "id", title = "说说id", type = "Integer")
    private Integer id;

    /**
     * 说说内容
     */
    @Schema(name = "content", title = "说说内容", type = "String")
    @NotBlank(message = "说说内容不能为空")
    private String content;

    /**
     * 图片
     */
    @Schema(name = "images", title = "说说图片", type = "String")
    private String images;

    /**
     * 是否置顶
     */
    @Schema(name = "isTop", title = "置顶状态", type = "Integer")
    @NotNull(message = "置顶状态不能为空")
    private Integer isTop;

    /**
     * 说说状态 1.公开 2.私密
     */
    @Schema(name = "status", title = "说说状态", type = "Integer")
    @NotNull(message = "说说状态不能为空")
    private Integer status;

}
