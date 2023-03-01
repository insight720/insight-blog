package pers.project.blog.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 角色菜单数据
 *
 * @author Luo Fei
 * @date 2023/1/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenuDTO {

    /**
     * 菜单 ID
     */
    private Integer id;

    /**
     * 菜单名
     */
    private String label;

    /**
     * 子菜单
     */
    private List<RoleMenuDTO> children;

}
