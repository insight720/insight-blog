package pers.project.blog.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 禁用角色数据
 *
 * @author Luo Fei
 * @version 2023/1/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisableRoleVO {

    @NotNull(message = "角色 ID 不能为空")
    @Schema(description = "角色 ID")
    private Integer roleId;

    @NotNull(message = "禁用状态不能为空")
    @Schema(description = "禁用状态（0 否 1 是）")
    private Integer isDisable;

}
