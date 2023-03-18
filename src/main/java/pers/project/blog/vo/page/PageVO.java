package pers.project.blog.vo.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 页面数据
 *
 * @author Luo Fei
 * @version 2023/1/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageVO {

    /**
     * 页面 ID
     */
    @Schema(description = "页面 ID")
    private Integer id;

    /**
     * 页面名
     */
    @NotBlank(message = "页面名称不能为空")
    @Schema(description = "页面名称")
    private String pageName;

    /**
     * 页面标签
     */
    @NotBlank(message = "页面标签不能为空")
    @Schema(description = "页面标签")
    private String pageLabel;

    /**
     * 页面封面
     */
    @NotBlank(message = "页面封面不能为空")
    @Schema(description = "页面封面")
    private String pageCover;

}
