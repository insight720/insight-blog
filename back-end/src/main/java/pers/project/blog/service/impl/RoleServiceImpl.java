package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pers.project.blog.constant.BooleanConstant;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.RoleDTO;
import pers.project.blog.dto.UserRoleDTO;
import pers.project.blog.entity.RoleEntity;
import pers.project.blog.entity.RoleMenuEntity;
import pers.project.blog.entity.RoleResourceEntity;
import pers.project.blog.entity.UserRoleEntity;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.handler.SecurityMetadataSourceHandler;
import pers.project.blog.mapper.RoleMapper;
import pers.project.blog.mapper.UserRoleMapper;
import pers.project.blog.service.RoleMenuService;
import pers.project.blog.service.RoleResourceService;
import pers.project.blog.service.RoleService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.RoleVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 针对表【tb_role】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-22
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements RoleService {

    private final RoleResourceService roleResourceService;

    private final RoleMenuService roleMenuService;

    private final UserRoleMapper userRoleMapper;

    public RoleServiceImpl(RoleResourceService roleResourceService,
                           RoleMenuService roleMenuService,
                           UserRoleMapper userRoleMapper) {
        this.roleResourceService = roleResourceService;
        this.roleMenuService = roleMenuService;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public PageDTO<RoleDTO> listRoles(ConditionVO conditionVO) {
        // 查询角色总量
        Long count = lambdaQuery().like(StringUtils.hasText(conditionVO.getKeywords()),
                RoleEntity::getRoleName, conditionVO.getKeywords()).count();
        if (count == 0) {
            return new PageDTO<>();
        }

        // 查询分页的角色列表
        IPage<?> page = PaginationUtils.getPage();
        List<RoleDTO> roleDTOList = baseMapper.listRoleDTOs
                (page.offset(), page.getSize(), conditionVO);
        return PageDTO.of(roleDTOList, count.intValue());
    }

    @Override
    public List<UserRoleDTO> listUserRoles() {
        List<RoleEntity> roleEntityList = lambdaQuery()
                .select(RoleEntity::getId, RoleEntity::getRoleName).list();
        return ConversionUtils.covertList(roleEntityList, UserRoleDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdateRole(RoleVO roleVO) {
        // 查询角色是否已经存在
        RoleEntity originalRole = lambdaQuery()
                .select(RoleEntity::getId, RoleEntity::getIsDisable)
                .eq(RoleEntity::getRoleName, roleVO.getRoleName())
                .one();

        RoleEntity newRole;
        if (originalRole == null) {
            newRole = RoleEntity.builder()
                    .roleName(roleVO.getRoleName())
                    .roleLabel(roleVO.getRoleLabel())
                    .isDisable(BooleanConstant.FALSE)
                    .build();
            save(newRole);
        } else {
            newRole = RoleEntity.builder()
                    .id(originalRole.getId())
                    .roleName(originalRole.getRoleName())
                    .roleLabel(roleVO.getRoleLabel())
                    .isDisable(originalRole.getIsDisable())
                    .build();
            updateById(newRole);
        }

        // 更新角色和资源的映射
        if (!CollectionUtils.isEmpty(roleVO.getResourceIdList())) {
            if (originalRole != null) {
                roleResourceService.lambdaUpdate()
                        .eq(RoleResourceEntity::getRoleId, originalRole.getId())
                        .remove();
            }

            List<RoleResourceEntity> roleResourceList = roleVO.getResourceIdList()
                    .stream()
                    .map(resourceId -> RoleResourceEntity.builder()
                            .roleId(newRole.getId())
                            .resourceId(resourceId)
                            .build())
                    .collect(Collectors.toList());
            roleResourceService.saveBatch(roleResourceList);

            SecurityMetadataSourceHandler.clearMetadataSource();
        }

        // 更新角色和菜单的映射
        if (!CollectionUtils.isEmpty(roleVO.getMenuIdList())) {
            if (originalRole != null) {
                roleMenuService.lambdaUpdate()
                        .eq(RoleMenuEntity::getRoleId, originalRole.getId())
                        .remove();
            }

            List<RoleMenuEntity> roleMenuList = roleVO.getMenuIdList()
                    .stream()
                    .map(menuId -> RoleMenuEntity.builder()
                            .roleId(newRole.getId())
                            .menuId(menuId)
                            .build())
                    .collect(Collectors.toList());
            roleMenuService.saveBatch(roleMenuList);
        }
    }

    // TODO: 2023/1/3 异常处理逻辑不明
    @Override
    public void removeRoles(List<Integer> roleIdList) {
        // 判断角色下是否又用户
        Long userRoleCount = new LambdaQueryChainWrapper<>(userRoleMapper)
                .select()
                .in(UserRoleEntity::getRoleId, roleIdList)
                .count();
        if (userRoleCount > 0) {
            throw new ServiceException("该角色下存在用户");
        }

        removeBatchByIds(roleIdList);
    }

}




