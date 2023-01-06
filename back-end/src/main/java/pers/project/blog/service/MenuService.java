package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.LabelOptionDTO;
import pers.project.blog.dto.MenuDTO;
import pers.project.blog.dto.UserMenuDTO;
import pers.project.blog.entity.MenuEntity;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.MenuVO;

import java.util.List;

/**
 * 针对表【tb_menu】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-28
 */
public interface MenuService extends IService<MenuEntity> {

    /**
     * 查看用户菜单
     *
     * @return 用户菜单列表
     */
    List<UserMenuDTO> listUserMenus();

    /**
     * 查看角色菜单选项
     *
     * @return 角色菜单选项列表
     */
    List<LabelOptionDTO> listMenuOptions();

    /**
     * 查看菜单列表
     *
     * @param conditionVO 条件
     * @return 菜单列表
     */
    List<MenuDTO> listMenus(ConditionVO conditionVO);

    /**
     * 新增或修改菜单
     *
     * @param menuVO 菜单信息
     */
    void saveOrUpdateMenu(MenuVO menuVO);

    /**
     * 删除菜单
     *
     * @param menuId 菜单id
     */
    void removeMenu(Integer menuId);

}
