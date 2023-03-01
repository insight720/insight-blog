package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.resource.ManageResourceDTO;
import pers.project.blog.dto.resource.RoleResourceDTO;
import pers.project.blog.entity.Resource;
import pers.project.blog.vo.resource.ResourceVO;

import java.util.List;

/**
 * 针对表【tb_resource】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-02
 */
public interface ResourceService extends IService<Resource> {

    /**
     * 获取角色资源权限数据
     */
    List<RoleResourceDTO> listRoleResources();

    /**
     * 获取资源管理数据
     */
    List<ManageResourceDTO> listManageResources(String keywords);

    /**
     * 新增或修改资源
     */
    void saveOrUpdateResource(ResourceVO resourceVO);

    /**
     * 删除资源
     */
    void removeResource(Integer resourceId);

}
