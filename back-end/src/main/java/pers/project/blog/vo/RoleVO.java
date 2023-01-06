package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 角色
 *
 * @author Luo Fei
 * @date 2023/1/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "角色")
public class RoleVO {

    // TODO: 2023/1/3 ID 不会从前端传过来
    /**
     * ID
     */
    @Schema(name = "id", title = "用户 ID", type = "Integer")
    private Integer id;

    /**
     * 标签名
     */
    @NotBlank(message = "角色名不能为空")
    @Schema(name = "roleName", title = "角色名", type = "String")
    private String roleName;

    /**
     * 标签名
     */
    @NotBlank(message = "权限标签不能为空")
    @Schema(name = "roleLabel", title = "标签名", type = "String")
    private String roleLabel;

    /**
     * 资源 ID 列表
     */
    @Schema(name = "resourceIdList", title = "资源列表", type = "List<Integer>")
    private List<Integer> resourceIdList;

    /**
     * 菜单 ID 列表
     */
    @Schema(name = "menuIdList", title = "菜单列表", type = "List<Integer>")
    private List<Integer> menuIdList;

}
