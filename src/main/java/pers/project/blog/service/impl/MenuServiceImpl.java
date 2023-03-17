package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.dto.menu.ManageMenuDTO;
import pers.project.blog.dto.menu.MenuBranchDTO;
import pers.project.blog.dto.menu.RoleMenuDTO;
import pers.project.blog.dto.menu.UserMenuDTO;
import pers.project.blog.entity.Menu;
import pers.project.blog.entity.RoleMenu;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.MenuMapper;
import pers.project.blog.mapper.RoleMenuMapper;
import pers.project.blog.service.MenuService;
import pers.project.blog.util.*;
import pers.project.blog.vo.menu.HiddenMenuVO;
import pers.project.blog.vo.menu.MenuVO;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static pers.project.blog.constant.CacheConst.MENU_WITHOUT_HIDDEN;
import static pers.project.blog.constant.CacheConst.MENU_WITH_HIDDEN;
import static pers.project.blog.constant.GenericConst.EMPTY_STR;
import static pers.project.blog.constant.GenericConst.TRUE_OF_INT;
import static pers.project.blog.constant.WebsiteConst.*;

/**
 * 针对表【tb_menu】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-28
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<UserMenuDTO> listUserMenus() {
        // 根据用户信息 ID 查询菜单
        Integer userInfoId = SecurityUtils.getUserInfoId();
        List<Menu> menuList = baseMapper.listMenusByUserInfoId(userInfoId);
        // 转换为用户菜单列表
        MenuBranchDTO menuBranchDTO = getMenuBranchDTO(menuList);
        return convertUserMenuList(menuBranchDTO);
    }

    @Override
    @Cacheable(cacheNames = MENU_WITHOUT_HIDDEN, key = "#root.methodName", sync = true)
    public List<RoleMenuDTO> listRoleMenus() {
        // 查询菜单数据列表
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Menu::getId, Menu::getName, Menu::getOrderNum, Menu::getParentId);
        List<Menu> munuList = list(queryWrapper);
        // 转换为角色菜单列表
        MenuBranchDTO menuBranchDTO = getMenuBranchDTO(munuList);
        return convertRoleMenuList(menuBranchDTO);
    }

    @Override
    @Cacheable(cacheNames = MENU_WITH_HIDDEN, key = "#root.methodName", sync = true,
            condition = "T(pers.project.blog.util.StrRegexUtils).isBlank(#keywords)")
    public List<ManageMenuDTO> listManageMenus(String keywords) {
        // 根据关键词查询菜单
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrRegexUtils.isNotBlank(keywords), Menu::getName, keywords);
        List<Menu> menuList = list(queryWrapper);
        // 转换为菜单管理数据列表
        MenuBranchDTO menuBranchDTO = getMenuBranchDTO(menuList);
        return convertManageMenuList(menuBranchDTO);
    }

    @Override
    @CacheEvict(cacheNames = {MENU_WITH_HIDDEN, MENU_WITHOUT_HIDDEN}, allEntries = true)
    public void saveOrUpdateMenu(MenuVO menuVO) {
        saveOrUpdate(BeanCopierUtils.copy(menuVO, Menu.class));
    }

    @Override
    @CacheEvict(cacheNames = MENU_WITH_HIDDEN, allEntries = true)
    public void updateHiddenStatus(HiddenMenuVO hiddenMenuVO) {
        // 菜单管理不能隐藏
        Integer menuId = hiddenMenuVO.getMenuId();
        if (menuId.equals(MENU_MANAGE_ID)
                || menuId.equals(AUTHORITY_MANAGE_ID)) {
            throw new ServiceException("菜单管理不能隐藏");
        }
        // 修改隐藏状态
        LambdaUpdateWrapper<Menu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Menu::getIsHidden, hiddenMenuVO.getIsHidden());
        updateWrapper.eq(Menu::getId, menuId);
        // 如果修改目录，同时修改目录下菜单
        if (hiddenMenuVO.getParentId() == null) {
            updateWrapper.or().eq(Menu::getParentId, menuId);
        }
        update(updateWrapper);
    }

    @Override
    @CacheEvict(cacheNames = {MENU_WITH_HIDDEN, MENU_WITHOUT_HIDDEN}, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void removeMenu(Integer menuId) {
        // 菜单管理不能删除
        if (menuId.equals(MENU_MANAGE_ID)
                || menuId.equals(AUTHORITY_MANAGE_ID)) {
            throw new ServiceException("菜单管理不能删除");
        }
        // 获取菜单分支和子菜单的 ID 列表
        List<Integer> menuIdList = getBranchAndSubmenuIdList(menuId);
        // 不能删除有角色关联的菜单
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(RoleMenu::getMenuId, menuIdList);
        boolean exists = roleMenuMapper.exists(queryWrapper);
        if (exists) {
            throw new ServiceException("有角色关联菜单或子菜单");
        }
        // 删除菜单和其子菜单
        removeBatchByIds(menuIdList);
    }

    /**
     * 获取菜单分支数据
     */
    private MenuBranchDTO getMenuBranchDTO(List<Menu> menuList) {
        // 获取分支 ID 和子菜单列表的映射
        CompletableFuture<Map<Integer, List<Menu>>> future
                = AsyncUtils.supplyAsync(() -> getBranchIdSubmenuMap(menuList));
        // 获取菜单分支数据
        List<Menu> catalogList = listBranchIds(menuList);
        Map<Integer, List<Menu>> branchIdSubmenuMap
                = AsyncUtils.get(future, "获取分支 ID 和子菜单列表的映射");
        return new MenuBranchDTO(catalogList, branchIdSubmenuMap);
    }

    /**
     * 获取分支和子菜单列表的映射
     */
    private Map<Integer, List<Menu>> getBranchIdSubmenuMap(List<Menu> menuList) {
        return menuList.stream()
                .filter(menu -> Objects.nonNull(menu.getParentId()))
                .collect(Collectors.groupingBy(Menu::getParentId));
    }

    /**
     * 获取分支 ID 列表
     */
    private List<Menu> listBranchIds(List<Menu> menuList) {
        return menuList.stream()
                .filter(menu -> Objects.isNull(menu.getParentId()))
                .sorted(Comparator.comparingInt(Menu::getOrderNum))
                .collect(Collectors.toList());
    }

    /**
     * 转换用户菜单数据列表
     */
    private List<UserMenuDTO> convertUserMenuList(MenuBranchDTO menuBranchDTO) {
        Map<Integer, List<Menu>> branchIdSubmenuMap = menuBranchDTO.getBranchIdSubmenuMap();
        List<Menu> menuBranchList = menuBranchDTO.getMenuBranchList();
        return menuBranchList.stream().map(branch -> {
            List<Menu> submenuList = branchIdSubmenuMap.get(branch.getId());
            if (CollectionUtils.isNotEmpty(submenuList)) {
                return convertCatalogAndSubmenu(branch, submenuList);
            } else {
                return convertEmptyCatalogOrLevelOneMenu(branch);
            }
        }).collect(Collectors.toList());
    }

    /**
     * 转换有子菜单的目录
     */
    private UserMenuDTO convertCatalogAndSubmenu(Menu catalog, List<Menu> submenuList) {
        List<UserMenuDTO> children = submenuList
                .stream()
                .sorted(Comparator.comparingInt(Menu::getOrderNum))
                .map(submenu -> {
                    UserMenuDTO child = BeanCopierUtils.copy(submenu, UserMenuDTO.class);
                    // 隐藏状态字段名不一样
                    child.setHidden(TRUE_OF_INT.equals(submenu.getIsHidden()));
                    return child;
                })
                .collect(Collectors.toList());
        UserMenuDTO userMenuDTO = BeanCopierUtils.copy(catalog, UserMenuDTO.class);
        userMenuDTO.setHidden(TRUE_OF_INT.equals(catalog.getIsHidden()));
        userMenuDTO.setChildren(children);
        return userMenuDTO;
    }

    /**
     * 转换空目录或一级菜单
     */
    private UserMenuDTO convertEmptyCatalogOrLevelOneMenu(Menu levelOneMenu) {
        // 转换为前端所需数据格式
        List<UserMenuDTO> children = Collections.singletonList
                (UserMenuDTO.builder()
                        .name(levelOneMenu.getName())
                        .path(EMPTY_STR)
                        .component(levelOneMenu.getComponent())
                        .icon(levelOneMenu.getIcon())
                        .build());
        return UserMenuDTO.builder()
                .path(levelOneMenu.getPath())
                .component(LAYOUT)
                .hidden(levelOneMenu.getIsHidden().equals(TRUE_OF_INT))
                .children(children)
                .build();
    }

    /**
     * 转换角色菜单数据列表
     */
    private List<RoleMenuDTO> convertRoleMenuList(MenuBranchDTO menuBranchDTO) {
        Map<Integer, List<Menu>> branchIdSubmenuMap = menuBranchDTO.getBranchIdSubmenuMap();
        List<Menu> menuBranchList = menuBranchDTO.getMenuBranchList();
        return menuBranchList.stream().map(branch -> {
            // 组装菜单分支和子菜单数据
            List<RoleMenuDTO> children = Optional.ofNullable
                            (branchIdSubmenuMap.get(branch.getId()))
                    .orElseGet(ArrayList::new)
                    .stream()
                    .map(child -> RoleMenuDTO.builder()
                            .id(child.getId())
                            .label(child.getName())
                            .build())
                    .collect(Collectors.toList());
            return RoleMenuDTO.builder()
                    .id(branch.getId())
                    .label(branch.getName())
                    .children(children)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 转换菜单管理数据列表
     */
    private List<ManageMenuDTO> convertManageMenuList(MenuBranchDTO menuBranchDTO) {
        Map<Integer, List<Menu>> branchIdSubmenuMap = menuBranchDTO.getBranchIdSubmenuMap();
        List<Menu> mebuBranchList = menuBranchDTO.getMenuBranchList();
        return mebuBranchList.stream().map(branch -> {
            // 组装菜单分支和子菜单数据
            List<ManageMenuDTO> children = ConvertUtils.convertList
                            (branchIdSubmenuMap.get(branch.getId()), ManageMenuDTO.class)
                    .stream()
                    .sorted(Comparator.comparingInt(ManageMenuDTO::getOrderNum))
                    .collect(Collectors.toList());
            ManageMenuDTO parent = BeanCopierUtils.copy(branch, ManageMenuDTO.class);
            parent.setChildren(children);
            return parent;
        }).sorted(Comparator.comparingInt(ManageMenuDTO::getOrderNum)).collect(Collectors.toList());
    }

    /**
     * 获取菜单分支和子菜单的 ID 列表
     */
    private List<Integer> getBranchAndSubmenuIdList(Integer menuId) {
        List<Integer> menuIdList = lambdaQuery()
                .select(Menu::getId)
                .eq(Menu::getParentId, menuId)
                .list().stream()
                .map(Menu::getId)
                .collect(Collectors.toList());
        menuIdList.add(menuId);
        return menuIdList;
    }

}




