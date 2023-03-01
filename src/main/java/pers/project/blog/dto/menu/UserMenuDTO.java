package pers.project.blog.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户菜单数据
 *
 * @author Luo Fei
 * @date 2022/12/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMenuDTO {

    /**
     * 菜单名
     */
    private String name;

    /**
     * 菜单路径
     */
    private String path;

    /**
     * 前端组件
     */
    private String component;

    /**
     * 菜单 ICON
     */
    private String icon;

    /**
     * 是否隐藏（0 否 1 是）
     */
    private Boolean hidden;

    /**
     * 子菜单列表
     */
    private List<UserMenuDTO> children;

}
