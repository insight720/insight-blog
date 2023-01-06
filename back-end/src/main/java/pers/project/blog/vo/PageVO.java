package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 页面信息
 *
 * @author Luo Fei
 * @date 2023/1/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "页面信息")
public class PageVO {

    /**
     * 页面 ID
     */
    @Schema(name = "id", title = "页面 ID", type = "Integer")
    private Integer id;

    /**
     * 页面名
     */
    @NotBlank(message = "页面名称不能为空")
    @Schema(name = "pageName", title = "页面名称", type = "String")
    private String pageName;

    /**
     * 页面标签
     */
    @NotBlank(message = "页面标签不能为空")
    @Schema(name = "pageLabel", title = "页面标签", type = "String")
    private String pageLabel;

    /**
     * 页面封面
     */
    @NotBlank(message = "页面封面不能为空")
    @Schema(name = "pageCover", title = "页面封面", type = "String")
    private String pageCover;

}
