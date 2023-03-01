package pers.project.blog.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色管理数据
 *
 * @author Luo Fei
 * @date 2022/12/31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManageRoleDTO {

    /**
     * 角色 ID
     */
    private Integer id;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 角色标签
     */
    private String roleLabel;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否禁用
     */
    private Integer isDisable;

    /**
     * 资源 ID 列表
     */
    @Schema
    private List<Integer> resourceIdList;

    /**
     * 菜单 ID 列表
     */
    @Schema
    private List<Integer> menuIdList;

}
