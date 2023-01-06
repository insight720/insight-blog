package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pers.project.blog.constant.BooleanConstant;
import pers.project.blog.dto.LabelOptionDTO;
import pers.project.blog.dto.ResourceDTO;
import pers.project.blog.entity.ResourceEntity;
import pers.project.blog.entity.RoleResourceEntity;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.handler.SecurityMetadataSourceHandler;
import pers.project.blog.mapper.ResourceMapper;
import pers.project.blog.mapper.RoleResourceMapper;
import pers.project.blog.service.ResourceService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.ResourceVO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 针对表【tb_resource】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-02
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, ResourceEntity> implements ResourceService {

    private final RoleResourceMapper roleResourceMapper;

    public ResourceServiceImpl(RoleResourceMapper roleResourceMapper) {
        this.roleResourceMapper = roleResourceMapper;
    }

    @Override
    public List<LabelOptionDTO> listResourceOptions() {
        // 查询资源列表
        List<ResourceEntity> resourceList = lambdaQuery()
                .select(ResourceEntity::getId, ResourceEntity::getResourceName, ResourceEntity::getParentId)
                .eq(ResourceEntity::getIsAnonymous, BooleanConstant.FALSE)
                .list();

        List<ResourceEntity> resourceModuleList = listResourceModules(resourceList);

        Map<Integer, List<ResourceEntity>> moduleIdChildrenMap = getModuleIdChildrenMap(resourceList);

        // 组装模块和其下资源
        return resourceModuleList.stream().map(resourceModule -> {
            List<LabelOptionDTO> children
                    = Optional.ofNullable(moduleIdChildrenMap.get(resourceModule.getId()))
                    .orElseGet(ArrayList::new)
                    .stream()
                    .map(child -> LabelOptionDTO.builder()
                            .id(child.getId())
                            .label(child.getResourceName())
                            .build())
                    .collect(Collectors.toList());

            return LabelOptionDTO.builder()
                    .id(resourceModule.getId())
                    .label(resourceModule.getResourceName())
                    .children(children)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<ResourceDTO> listResources(ConditionVO conditionVO) {
        // 查询资源列表
        List<ResourceEntity> resourceList = lambdaQuery()
                .like(StringUtils.hasText(conditionVO.getKeywords()),
                        ResourceEntity::getResourceName, conditionVO.getKeywords())
                .list();

        List<ResourceEntity> resourceModuleList = listResourceModules(resourceList);

        Map<Integer, List<ResourceEntity>> moduleIdChildrenMap = getModuleIdChildrenMap(resourceList);

        // 绑定模块下的所有接口资源
        List<ResourceDTO> resourceDTOList = resourceModuleList.stream().map(resourceModule -> {
            List<ResourceDTO> children = ConversionUtils.covertList
                    (moduleIdChildrenMap.remove(resourceModule.getId()), ResourceDTO.class);

            ResourceDTO resourceDTO = ConversionUtils.convertObject(resourceModule, ResourceDTO.class);
            resourceDTO.setChildren(children);
            return resourceDTO;
        }).collect(Collectors.toList());

        // TODO: 2023/1/3 逻辑不明
        // 不属于任何模块的接口资源
        if (!CollectionUtils.isEmpty(moduleIdChildrenMap)) {
            List<ResourceDTO> unboundResourceList = moduleIdChildrenMap.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .map(resource -> ConversionUtils.convertObject(resource, ResourceDTO.class))
                    .collect(Collectors.toList());
            resourceDTOList.addAll(unboundResourceList);
        }

        return resourceDTOList;
    }

    @Override
    public void saveOrUpdateResource(ResourceVO resourceVO) {
        ResourceEntity resourceEntity = ConversionUtils.convertObject(resourceVO, ResourceEntity.class);
        saveOrUpdate(resourceEntity);

        SecurityMetadataSourceHandler.clearMetadataSource();
    }

    // TODO: 2023/1/3 异常处理逻辑不明

    @Override
    public void removeResource(Integer resourceId) {
        // 查询是否有角色关联
        Long roleResourceCount = new LambdaQueryChainWrapper<>(roleResourceMapper)
                .select().eq(RoleResourceEntity::getResourceId, resourceId).count();
        if (roleResourceCount > 0) {
            throw new ServiceException("角色下存在资源");
        }

        // 删除资源和其子资源
        lambdaUpdate().eq(ResourceEntity::getId, resourceId)
                .or().eq(ResourceEntity::getParentId, resourceId)
                .remove();
    }

    /**
     * 获取模块 ID 与模块下资源的映射
     *
     * @param resourceList 资源列表
     * @return 模块 ID 与模块下资源的映射
     */
    private Map<Integer, List<ResourceEntity>> getModuleIdChildrenMap(List<ResourceEntity> resourceList) {
        return resourceList.stream()
                .filter(resourceEntity -> Objects.nonNull(resourceEntity.getParentId()))
                .collect(Collectors.groupingBy(ResourceEntity::getParentId));
    }

    /**
     * 获取所有资源模块
     *
     * @param resourceList 资源列表
     * @return 资源模块列表
     */
    private List<ResourceEntity> listResourceModules(List<ResourceEntity> resourceList) {
        return resourceList.stream()
                .filter(resourceEntity -> Objects.isNull(resourceEntity.getParentId()))
                .collect(Collectors.toList());
    }

}




