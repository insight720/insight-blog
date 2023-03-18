package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.role.ManageRoleDTO;
import pers.project.blog.dto.role.UserRoleDTO;
import pers.project.blog.entity.Role;
import pers.project.blog.vo.role.DisableRoleVO;
import pers.project.blog.vo.role.RoleVO;

import java.util.List;

/**
 * 针对表【tb_role】的数据库操作 Service
 *
 * @author Luo Fei
 * @version 2022-12-22
 */
public interface RoleService extends IService<Role> {

    /**
     * 获取用户列表角色数据
     */
    List<UserRoleDTO> listUserRoles();

    /**
     * 获取分页的角色管理数据
     */
    PageDTO<ManageRoleDTO> listManageRoles(String keywords);

    /**
     * 保存或更新角色
     */
    void saveOrUpdateRole(RoleVO roleVO);

    /**
     * 修改角色禁用状态
     */
    void updateDisableStatus(DisableRoleVO disableRoleVO);

    /**
     * 删除角色
     */
    void removeRoles(List<Integer> roleIdList);

}
