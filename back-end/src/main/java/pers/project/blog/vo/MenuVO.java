package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 菜单信息
 *
 * @author Luo Fei
 * @date 2023/1/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "菜单信息")
public class MenuVO {

    /**
     * ID
     */
    @Schema(name = "id", title = "菜单 ID", type = "Integer")
    private Integer id;

    /**
     * 菜单名
     */
    @NotBlank(message = "菜单名不能为空")
    @Schema(name = "name", title = "菜单名", type = "String")
    private String name;

    /**
     * icon
     */
    @NotBlank(message = "菜单icon不能为空")
    @Schema(name = "icon", title = "菜单icon", type = "String")
    private String icon;

    /**
     * 路径
     */
    @NotBlank(message = "路径不能为空")
    @Schema(name = "path", title = "路径", type = "String")
    private String path;

    /**
     * 组件
     */
    @NotBlank(message = "组件不能为空")
    @Schema(name = "component", title = "组件", type = "String")
    private String component;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    @Schema(name = "orderNum", title = "排序", type = "Integer")
    private Integer orderNum;

    /**
     * 父 ID
     */
    @Schema(name = "parentId", title = "父id", type = "Integer")
    private Integer parentId;

    /**
     * 是否隐藏
     */
    @Schema(name = "isHidden", title = "是否隐藏", type = "Integer")
    private Integer isHidden;

}
