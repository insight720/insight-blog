package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.role.ManageRoleDTO;
import pers.project.blog.dto.role.ResourceRoleDTO;
import pers.project.blog.entity.Role;

import java.util.List;

/**
 * 针对表【tb_role】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @version 2022-12-22
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 按用户信息 ID 列出权限角色
     * <p>
     * (被禁用的权限角色不会列出）
     */
    List<String> listAuthorityRoles(@Param("userInfoId") Integer userInfoId);

    /**
     * 查询接口资源和授权角色信息，用作授权的依据。
     * <p>
     * （被禁用的角色没有权限）
     */
    List<ResourceRoleDTO> listResourceRoles();

    /**
     * 查询分页的角色管理数据
     */
    List<ManageRoleDTO> listManageRoles(@Param("offset") long offset,
                                        @Param("size") long size,
                                        @Param("keywords") String keywords);

}




