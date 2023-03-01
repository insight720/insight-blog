package pers.project.blog.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 菜单数据
 *
 * @author Luo Fei
 * @date 2023/1/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuVO {

    /**
     * 菜单 ID
     */
    @Schema(description = "菜单 ID")
    private Integer id;

    /**
     * 菜单名
     */
    @NotBlank(message = "菜单名不能为空")
    @Schema(description = "菜单名")
    private String name;

    /**
     * Icon
     */
    @NotBlank(message = "菜单 Icon 不能为空")
    @Schema(description = "菜单 Icon")
    private String icon;

    /**
     * 路径
     */
    @NotBlank(message = "路径不能为空")
    @Schema(description = "路径")
    private String path;

    /**
     * 组件
     */
    @NotBlank(message = "组件不能为空")
    @Schema(description = "组件")
    private String component;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    @Schema(description = "排序")
    private Integer orderNum;

    /**
     * 父 ID
     */
    @Schema(description = "父 ID")
    private Integer parentId;

    /**
     * 是否隐藏（0 否 1 是）
     */
    @Schema(description = "是否隐藏")
    private Integer isHidden;

}
