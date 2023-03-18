package pers.project.blog.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 隐藏菜单数据
 *
 * @author Luo Fei
 * @version 2023/1/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HiddenMenuVO {

    @NotNull(message = "菜单 ID 不能为空")
    @Schema(description = "菜单 ID")
    private Integer menuId;

    @NotNull(message = "隐藏状态不能为空")
    @Schema(description = "隐藏状态（0 否 1 是）")
    private Integer isHidden;

    @Schema(description = "菜单目录 ID")
    private Integer parentId;

}
