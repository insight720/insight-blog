package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.ResourceRoleDTO;
import pers.project.blog.dto.RoleDTO;
import pers.project.blog.entity.RoleEntity;
import pers.project.blog.vo.ConditionVO;

import java.util.List;

/**
 * 针对表【tb_role】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-22
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    /**
     * 查询路由角色列表
     *
     * @return 角色标签
     */
    List<ResourceRoleDTO> listResourceRoles();


    /**
     * 按用户账号信息 ID 列出角色标签
     *
     * @param userInfoId 用户账号信息 ID
     * @return 角色标签列表
     */
    List<String> listRoleLabelsByUserInfoId(@Param("userInfoId") Integer userInfoId);

    /**
     * 查询角色列表
     *
     * @param offset 当前分页偏移量
     * @param size   条件
     * @return 角色列表
     */
    List<RoleDTO> listRoleDTOs(@Param("offset") Long offset, @Param("size") Long size,
                               @Param("conditionVO") ConditionVO conditionVO);

}




