package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pers.project.blog.constant.BooleanConstant;
import pers.project.blog.constant.FrontEndConstant;
import pers.project.blog.dto.LabelOptionDTO;
import pers.project.blog.dto.MenuDTO;
import pers.project.blog.dto.UserMenuDTO;
import pers.project.blog.entity.MenuEntity;
import pers.project.blog.entity.RoleMenuEntity;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.MenuMapper;
import pers.project.blog.mapper.RoleMenuMapper;
import pers.project.blog.service.MenuService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.SecurityUtils;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.MenuVO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 针对表【tb_menu】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-28
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuEntity> implements MenuService {

    private final RoleMenuMapper roleMenuMapper;

    public MenuServiceImpl(RoleMenuMapper roleMenuMapper) {
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public List<UserMenuDTO> listUserMenus() {
        List<MenuEntity> menuList = baseMapper.listMenusByUserInfoId
                (SecurityUtils.getUserDetails().getUserInfoId());

        List<MenuEntity> catalogList = listCatalogs(menuList);

        Map<Integer, List<MenuEntity>> catalogSubmenuMap = getCatalogSubmenuMap(menuList);

        return convertUserMenuList(catalogList, catalogSubmenuMap);
    }

    @Override
    public List<LabelOptionDTO> listMenuOptions() {
        // 查询菜单数据列表
        List<MenuEntity> menuList = lambdaQuery()
                .select(MenuEntity::getId, MenuEntity::getName, MenuEntity::getOrderNum, MenuEntity::getParentId)
                .list();

        List<MenuEntity> catalogList = listCatalogs(menuList);

        Map<Integer, List<MenuEntity>> catalogSubmenuMap = getCatalogSubmenuMap(menuList);

        // 组装目录和其子菜单
        return catalogList.stream().map(catalog -> {
            List<LabelOptionDTO> children
                    = Optional.ofNullable(catalogSubmenuMap.get(catalog.getId()))
                    .orElseGet(ArrayList::new)
                    .stream()
                    .map(child -> LabelOptionDTO.builder()
                            .id(child.getId())
                            .label(child.getName())
                            .build())
                    .collect(Collectors.toList());

            return LabelOptionDTO.builder()
                    .id(catalog.getId())
                    .label(catalog.getName())
                    .children(children)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<MenuDTO> listMenus(ConditionVO conditionVO) {
        // 查询菜单数据列表
        List<MenuEntity> menuList = lambdaQuery()
                .like(StringUtils.hasText(conditionVO.getKeywords()),
                        MenuEntity::getName, conditionVO.getKeywords()).list();

        List<MenuEntity> catalogList = listCatalogs(menuList);

        Map<Integer, List<MenuEntity>> catalogSubmenuMap = getCatalogSubmenuMap(menuList);

        // 绑定目录和子菜单
        List<MenuDTO> menuDTOList = catalogList.stream().map(catalog -> {
            List<MenuDTO> children = ConversionUtils.covertList
                            (catalogSubmenuMap.remove(catalog.getId()), MenuDTO.class)
                    .stream()
                    .sorted(Comparator.comparingInt(MenuDTO::getOrderNum))
                    .collect(Collectors.toList());

            MenuDTO menuDTO = ConversionUtils.convertObject(catalog, MenuDTO.class);
            menuDTO.setChildren(children);
            return menuDTO;
        }).sorted(Comparator.comparingInt(MenuDTO::getOrderNum)).collect(Collectors.toList());

        // TODO: 2023/1/3 逻辑不明
        // 不属于任何目录的菜单
        if (CollectionUtils.isNotEmpty(catalogSubmenuMap)) {
            List<MenuDTO> unboundMenuList = catalogSubmenuMap.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .map(menu -> ConversionUtils.convertObject(menu, MenuDTO.class))
                    .sorted(Comparator.comparingInt(MenuDTO::getOrderNum))
                    .collect(Collectors.toList());
            menuDTOList.addAll(unboundMenuList);
        }

        return menuDTOList;
    }

    @Override
    public void saveOrUpdateMenu(MenuVO menuVO) {
        saveOrUpdate(ConversionUtils.convertObject(menuVO, MenuEntity.class));
    }

    @Override
    public void removeMenu(Integer menuId) {
        // 查询受否有角色关联
        Long roleMenuCount = new LambdaQueryChainWrapper<>(roleMenuMapper)
                .select()
                .eq(RoleMenuEntity::getMenuId, menuId)
                .count();
        if (roleMenuCount > 0) {
            throw new ServiceException("菜单有关联角色");
        }

        // 删除菜单和其子菜单
        lambdaUpdate().eq(MenuEntity::getId, menuId)
                .or().eq(MenuEntity::getParentId, menuId)
                .remove();
    }

    /**
     * 获取目录列表
     *
     * @param menuList 菜单列表
     * @return 排序后的目录列表
     */
    private List<MenuEntity> listCatalogs(List<MenuEntity> menuList) {
        return menuList.stream()
                .filter(menuEntity -> Objects.isNull(menuEntity.getParentId()))
                .sorted(Comparator.comparingInt(MenuEntity::getOrderNum))
                .collect(Collectors.toList());
    }

    /**
     * 获取目录和子菜单的映射
     *
     * @param menuList 菜单列表
     * @return 目录和子菜单的映射
     */
    private Map<Integer, List<MenuEntity>> getCatalogSubmenuMap(List<MenuEntity> menuList) {
        return menuList.stream()
                .filter(menuEntity -> Objects.nonNull(menuEntity.getParentId()))
                .collect(Collectors.groupingBy(MenuEntity::getParentId));
    }

    /**
     * 转换用户菜单列表
     *
     * @param catalogList       目录列表
     * @param catalogSubmenuMap 目录和子菜单的映射
     * @return 前端用户菜单数据列表
     */
    private List<UserMenuDTO> convertUserMenuList(List<MenuEntity> catalogList,
                                                  Map<Integer, List<MenuEntity>> catalogSubmenuMap) {
        return catalogList.stream().map(catalog -> {
            List<MenuEntity> submenuList = catalogSubmenuMap.get(catalog.getId());

            // 一级菜单处理
            if (CollectionUtils.isEmpty(submenuList)) {
                List<UserMenuDTO> children = Collections.singletonList
                        (UserMenuDTO.builder()
                                .name(catalog.getName())
                                .path("")
                                .component(catalog.getComponent())
                                .icon(catalog.getIcon())
                                .build());
                return UserMenuDTO.builder()
                        .path(catalog.getPath())
                        .component(FrontEndConstant.LAYOUT)
                        .hidden(BooleanConstant.TRUE.equals(catalog.getIsHidden()))
                        .children(children)
                        .build();
            }

            // 多级菜单处理
            List<UserMenuDTO> children = submenuList.stream()
                    .sorted(Comparator.comparingInt(MenuEntity::getOrderNum))
                    .map(menuEntity -> {
                        UserMenuDTO child = ConversionUtils.convertObject(menuEntity, UserMenuDTO.class);
                        child.setHidden(BooleanConstant.TRUE.equals(menuEntity.getIsHidden()));
                        return child;
                    })
                    .collect(Collectors.toList());
            UserMenuDTO userMenuDTO = ConversionUtils.convertObject(catalog, UserMenuDTO.class);
            userMenuDTO.setHidden(BooleanConstant.TRUE.equals(catalog.getIsHidden()));
            userMenuDTO.setChildren(children);

            return userMenuDTO;
        }).collect(Collectors.toList());
    }

}




