package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.menu.ManageMenuDTO;
import pers.project.blog.dto.menu.RoleMenuDTO;
import pers.project.blog.dto.menu.UserMenuDTO;
import pers.project.blog.entity.Menu;
import pers.project.blog.vo.menu.HiddenMenuVO;
import pers.project.blog.vo.menu.MenuVO;

import java.util.List;

/**
 * 针对表【tb_menu】的数据库操作 Service
 *
 * @author Luo Fei
 * @version 2022-12-28
 */
public interface MenuService extends IService<Menu> {

    /**
     * 获取用户菜单数据
     */
    List<UserMenuDTO> listUserMenus();

    /**
     * 获取角色菜单数据
     */
    List<RoleMenuDTO> listRoleMenus();

    /**
     * 获取菜单管理数据
     */
    List<ManageMenuDTO> listManageMenus(String keywords);

    /**
     * 新增或修改菜单
     */
    void saveOrUpdateMenu(MenuVO menuVO);

    /**
     * 修改菜单隐藏状态
     */
    void updateHiddenStatus(HiddenMenuVO hiddenMenuVO);

    /**
     * 删除菜单
     */
    void removeMenu(Integer menuId);

}
