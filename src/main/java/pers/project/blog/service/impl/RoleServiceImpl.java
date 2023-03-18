package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.role.ManageRoleDTO;
import pers.project.blog.dto.role.UserRoleDTO;
import pers.project.blog.entity.Role;
import pers.project.blog.entity.RoleMenu;
import pers.project.blog.entity.RoleResource;
import pers.project.blog.entity.UserRole;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.RoleMapper;
import pers.project.blog.mapper.UserRoleMapper;
import pers.project.blog.service.RoleMenuService;
import pers.project.blog.service.RoleResourceService;
import pers.project.blog.service.RoleService;
import pers.project.blog.util.*;
import pers.project.blog.vo.role.DisableRoleVO;
import pers.project.blog.vo.role.RoleVO;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static pers.project.blog.constant.CacheConst.ROLE;
import static pers.project.blog.constant.GenericConst.ZERO_L;
import static pers.project.blog.constant.WebsiteConst.ADMIN_ID;

/**
 * 针对表【tb_role】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @version 2022-12-22
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RoleMenuService roleMenuService;
    @Resource
    private RoleResourceService roleResourceService;

    @Override
    @Cacheable(cacheNames = ROLE, key = "#root.methodName", sync = true)
    public List<UserRoleDTO> listUserRoles() {
        List<Role> roleList = lambdaQuery()
                .select(Role::getId, Role::getRoleName).list();
        return ConvertUtils.convertList(roleList, UserRoleDTO.class);
    }

    @Override
    public PageDTO<ManageRoleDTO> listManageRoles(String keywords) {
        // 根据关键词查询角色总量
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrRegexUtils.isNotBlank(keywords),
                Role::getRoleName, keywords);
        long count = count(queryWrapper);
        if (count == ZERO_L) {
            return new PageDTO<>();
        }
        // 查询分页的角色管理数据列表
        List<ManageRoleDTO> manageRoleList = baseMapper.listManageRoles
                (PageUtils.offset(), PageUtils.size(), keywords);
        return PageUtils.build(manageRoleList, count);
    }

    @Override
    @CacheEvict(cacheNames = ROLE, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdateRole(RoleVO roleVO) {
        Integer originalRoleId = roleVO.getId();
        List<Integer> menuIdList = roleVO.getMenuIdList();
        List<Integer> resourceIdList = roleVO.getResourceIdList();
        // 保存或更新角色
        if (originalRoleId == null) {
            // 角色名为授权依据，不能重复
            boolean exists = lambdaQuery()
                    .eq(Role::getRoleName, roleVO.getRoleName())
                    .exists();
            if (exists) {
                throw new ServiceException("角色名已存在");
            }
        } else {
            // 删除角色原来的映射
            removeSingleRoleMap(originalRoleId, menuIdList, resourceIdList);
        }
        Role role = BeanCopierUtils.copy(roleVO, Role.class);
        saveOrUpdate(role);
        // 保存角色的新映射
        saveRoleMap(role.getId(), menuIdList, resourceIdList);
        // 清除 Spring Security 的授权凭据
        SecurityUtils.clearAuthorizationCredentials();
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateDisableStatus(DisableRoleVO disableRoleVO) {
        // 不能禁用管理员
        Integer roleId = disableRoleVO.getRoleId();
        if (roleId.equals(ADMIN_ID)) {
            throw new ServiceException("不能禁用管理员");
        }
        // 修改禁用状态
        LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Role::getIsDisable, disableRoleVO.getIsDisable());
        updateWrapper.eq(Role::getId, roleId);
        update(updateWrapper);
        // 清除 Spring Security 的授权凭据
        SecurityUtils.clearAuthorizationCredentials();
    }

    @Override
    @CacheEvict(cacheNames = ROLE, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void removeRoles(List<Integer> roleIdList) {
        // 不能删除有用户关联的角色
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserRole::getRoleId, roleIdList);
        boolean exists = userRoleMapper.exists(queryWrapper);
        if (exists) {
            throw new ServiceException("有用户关联角色");
        }
        // 删除角色
        removeBatchByIds(roleIdList);
        // 删除角色的映射
        removeMultiRoleMap(roleIdList);
        // 无用户关联角色，可以不清除 Spring Security 的授权凭据
    }

    /**
     * 删除单个角色的映射
     */
    private void removeSingleRoleMap(Integer originalRoleId, List<Integer> menuIdList,
                                     List<Integer> resourceIdList) {
        if (CollectionUtils.isNotEmpty(menuIdList)) {
            // 删除角色与菜单的映射
            roleMenuService.lambdaUpdate()
                    .eq(RoleMenu::getRoleId, originalRoleId)
                    .remove();
        } else if (CollectionUtils.isNotEmpty(resourceIdList)) {
            // 删除角色与资源的映射
            roleResourceService.lambdaUpdate()
                    .eq(RoleResource::getRoleId, originalRoleId)
                    .remove();
        }
    }

    /**
     * 保存角色的映射
     */
    private void saveRoleMap(Integer roleId, List<Integer> menuIdList,
                             List<Integer> resourceIdList) {
        // 保存角色与菜单的映射
        if (CollectionUtils.isNotEmpty(menuIdList)) {
            List<RoleMenu> roleMenuList = menuIdList
                    .stream()
                    .map(menuId -> RoleMenu.builder()
                            .roleId(roleId)
                            .menuId(menuId)
                            .build())
                    .collect(Collectors.toList());
            roleMenuService.saveBatch(roleMenuList);
        }
        // 保存角色与资源的映射
        if (CollectionUtils.isNotEmpty(resourceIdList)) {
            List<RoleResource> roleResourceList = resourceIdList
                    .stream()
                    .map(resourceId -> RoleResource.builder()
                            .roleId(roleId)
                            .resourceId(resourceId)
                            .build())
                    .collect(Collectors.toList());
            roleResourceService.saveBatch(roleResourceList);
        }
    }

    /**
     * 删除多个角色的映射
     */
    private void removeMultiRoleMap(List<Integer> roleIdList) {
        // Mybatis-Plus SQL 用 IN 时，列表不能为空
        if (CollectionUtils.isEmpty(roleIdList)) {
            return;
        }
        // 删除角色与菜单的映射
        roleMenuService.lambdaUpdate()
                .in(RoleMenu::getRoleId, roleIdList)
                .remove();
        // 删除角色与资源的映射
        roleResourceService.lambdaUpdate()
                .in(RoleResource::getRoleId, roleIdList)
                .remove();
    }

}




