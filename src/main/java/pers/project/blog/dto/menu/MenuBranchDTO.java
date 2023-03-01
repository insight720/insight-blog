package pers.project.blog.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.blog.entity.Menu;

import java.util.List;
import java.util.Map;

/**
 * 菜单分支数据
 * <p>
 * 菜单分支是目录或者一级菜单。
 *
 * @author Luo Fei
 * @date 2023/1/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuBranchDTO {

    /**
     * 菜单分支
     */
    private List<Menu> menuBranchList;

    /**
     * 菜单分支 ID 与子菜单数据的映射
     */
    private Map<Integer, List<Menu>> branchIdSubmenuMap;

}
