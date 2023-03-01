package pers.project.blog.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 角色数据
 *
 * @author Luo Fei
 * @date 2023/1/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO {

    /**
     * 角色 ID
     */
    @Schema(description = "用户 ID")
    private Integer id;

    /**
     * 角色名
     */
    @NotBlank(message = "角色名不能为空")
    @Schema(description = "角色名")
    private String roleName;

    /**
     * 标签名
     */
    @NotBlank(message = "权限标签不能为空")
    @Schema(description = "标签名")
    private String roleLabel;

    /**
     * 禁用状态
     */
    @Schema(description = "禁用状态（0 否 1 是）")
    private Integer isDisable;

    /**
     * 资源 ID 列表
     */
    @Schema(description = "资源列表")
    private List<Integer> resourceIdList;

    /**
     * 菜单 ID 列表
     */
    @Schema(description = "菜单列表")
    private List<Integer> menuIdList;

}
