package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.LabelOptionDTO;
import pers.project.blog.dto.ResourceDTO;
import pers.project.blog.entity.ResourceEntity;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.ResourceVO;

import java.util.List;

/**
 * 针对表【tb_resource】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-02
 */
public interface ResourceService extends IService<ResourceEntity> {

    /**
     * 查看资源选项
     *
     * @return 资源选项列表
     */
    List<LabelOptionDTO> listResourceOptions();

    /**
     * 查看资源列表
     *
     * @param conditionVO 条件
     * @return 资源列表
     */
    List<ResourceDTO> listResources(ConditionVO conditionVO);

    /**
     * 新增或修改资源
     *
     * @param resourceVO 资源信息
     */
    void saveOrUpdateResource(ResourceVO resourceVO);

    /**
     * 删除资源
     *
     * @param resourceId 资源 ID
     */
    void removeResource(Integer resourceId);

}
