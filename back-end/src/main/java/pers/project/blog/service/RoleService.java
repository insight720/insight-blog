package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.RoleDTO;
import pers.project.blog.dto.UserRoleDTO;
import pers.project.blog.entity.RoleEntity;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.RoleVO;

import java.util.List;

/**
 * 针对表【tb_role】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-22
 */
public interface RoleService extends IService<RoleEntity> {

    /**
     * 查询角色列表
     *
     * @param conditionVO 条件
     * @return 分页的视图对象
     */
    PageDTO<RoleDTO> listRoles(ConditionVO conditionVO);

    /**
     * 查询用户角色选项
     *
     * @param conditionVO 条件
     * @return 角色选项列表
     */
    List<UserRoleDTO> listUserRoles();

    /**
     * 保存或更新角色
     *
     * @param roleVO 角色信息
     */
    void saveOrUpdateRole(RoleVO roleVO);

    /**
     * 删除角色
     *
     * @param roleIdList 角色id列表
     */
    void removeRoles(List<Integer> roleIdList);

}
