package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.dto.resource.ManageResourceDTO;
import pers.project.blog.dto.resource.ResourceModuleDTO;
import pers.project.blog.dto.resource.RoleResourceDTO;
import pers.project.blog.entity.Resource;
import pers.project.blog.entity.RoleResource;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.ResourceMapper;
import pers.project.blog.mapper.RoleResourceMapper;
import pers.project.blog.service.ResourceService;
import pers.project.blog.util.AsyncUtils;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.util.SecurityUtils;
import pers.project.blog.util.StrRegexUtils;
import pers.project.blog.vo.resource.ResourceVO;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static pers.project.blog.constant.CacheConst.RESOURCE;
import static pers.project.blog.constant.GenericConst.FALSE_OF_INT;

/**
 * 针对表【tb_resource】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-02
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    @javax.annotation.Resource
    private RoleResourceMapper roleResourceMapper;

    @Override
    @Cacheable(cacheNames = RESOURCE, key = "#root.methodName", sync = true)
    public List<RoleResourceDTO> listRoleResources() {
        // 查询资源数据列表
        List<Resource> resourceList = lambdaQuery()
                .select(Resource::getId, Resource::getResourceName, Resource::getParentId)
                .eq(Resource::getIsAnonymous, FALSE_OF_INT)
                .list();
        // 转换为角色资源列表
        ResourceModuleDTO resourceModuleDTO = getResourceModuleDTO(resourceList);
        return convertRoleResourceList(resourceModuleDTO);
    }

    @Override
    @Cacheable(cacheNames = RESOURCE, key = "#root.methodName", sync = true,
            condition = "T(pers.project.blog.util.StrRegexUtils).isBlank(#keywords)")
    public List<ManageResourceDTO> listManageResources(String keywords) {
        // 根据关键词查询资源
        LambdaQueryWrapper<Resource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrRegexUtils.isNotBlank(keywords),
                Resource::getResourceName, keywords);
        // 转换为资源管理数据列表
        List<Resource> resourceList = list(queryWrapper);
        // 转换为资源管理数据列表
        ResourceModuleDTO resourceModuleDTO = getResourceModuleDTO(resourceList);
        return convertManageResourceList(resourceModuleDTO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = RESOURCE, allEntries = true)
    public void saveOrUpdateResource(ResourceVO resourceVO) {
        // 新增或修改资源
        Resource resource = ConvertUtils.convert(resourceVO, Resource.class);
        saveOrUpdate(resource);
        // 清除 Spring Security 的授权凭据
        SecurityUtils.clearAuthorizationCredentials();
    }

    @Override
    @CacheEvict(cacheNames = RESOURCE, allEntries = true)
    public void removeResource(Integer resourceId) {
        // 获取资源模块和子资源 ID 列表
        List<Integer> resourceIdList = getModuleAndChildrenIdList(resourceId);
        // 不能删除有角色关联的资源
        LambdaQueryWrapper<RoleResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(RoleResource::getResourceId, resourceIdList);
        boolean exists = roleResourceMapper.exists(queryWrapper);
        if (exists) {
            throw new ServiceException("有角色关联资源或子资源");
        }
        // 删除资源和其子资源
        removeBatchByIds(resourceIdList);
    }

    /**
     * 获取菜单模块数据
     */
    private ResourceModuleDTO getResourceModuleDTO(List<Resource> resourceList) {
        // 获取模块 ID 与模块下资源的映射
        CompletableFuture<Map<Integer, List<Resource>>> future
                = AsyncUtils.supplyAsync(() -> getModuleIdChildrenMap(resourceList));
        // 获取资源模块数据
        List<Resource> resourceModuleList = listResourceModules(resourceList);
        Map<Integer, List<Resource>> moduleIdChildrenMap
                = AsyncUtils.get(future, "获取模块 ID 与模块下资源的映射");
        return new ResourceModuleDTO(resourceModuleList, moduleIdChildrenMap);
    }

    /**
     * 获取模块 ID 与模块下资源的映射
     */
    private Map<Integer, List<Resource>> getModuleIdChildrenMap(List<Resource> resourceList) {
        return resourceList.stream()
                .filter(resource -> Objects.nonNull(resource.getParentId()))
                .collect(Collectors.groupingBy(Resource::getParentId));
    }

    /**
     * 获取资源模块数据
     */
    private List<Resource> listResourceModules(List<Resource> resourceList) {
        return resourceList.stream()
                .filter(resource -> Objects.isNull(resource.getParentId()))
                .collect(Collectors.toList());
    }

    /**
     * 转换角色资源列表
     */
    private List<RoleResourceDTO> convertRoleResourceList(ResourceModuleDTO resourceModuleDTO) {
        Map<Integer, List<Resource>> moduleIdChildrenMap = resourceModuleDTO.getModuleIdChildrenMap();
        List<Resource> resourceModuleList = resourceModuleDTO.getResourceModuleList();
        // 组装资源模块和子资源数据
        return resourceModuleList.stream().map(module -> {
            List<RoleResourceDTO> children = Optional.ofNullable
                            (moduleIdChildrenMap.get(module.getId()))
                    .orElseGet(ArrayList::new)
                    .stream()
                    .map(child -> RoleResourceDTO.builder()
                            .id(child.getId())
                            .label(child.getResourceName())
                            .build())
                    .collect(Collectors.toList());
            return RoleResourceDTO.builder()
                    .id(module.getId())
                    .label(module.getResourceName())
                    .children(children)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 转换资源管理数据列表
     */
    private List<ManageResourceDTO> convertManageResourceList(ResourceModuleDTO resourceModuleDTO) {
        Map<Integer, List<Resource>> moduleIdChildrenMap = resourceModuleDTO.getModuleIdChildrenMap();
        List<Resource> resourceModuleList = resourceModuleDTO.getResourceModuleList();
        // 组装资源模块和子资源数据
        return resourceModuleList.stream().map(module -> {
            List<ManageResourceDTO> children = ConvertUtils.convertList
                    (moduleIdChildrenMap.get(module.getId()), ManageResourceDTO.class);
            ManageResourceDTO manageResourceDTO = ConvertUtils.convert
                    (module, ManageResourceDTO.class);
            manageResourceDTO.setChildren(children);
            return manageResourceDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 获取资源模块和子资源 ID 列表
     */
    private List<Integer> getModuleAndChildrenIdList(Integer moduleId) {
        List<Integer> resourceIdList = lambdaQuery()
                .select(Resource::getId)
                .eq(Resource::getParentId, moduleId)
                .list().stream()
                .map(Resource::getId)
                .collect(Collectors.toList());
        resourceIdList.add(moduleId);
        return resourceIdList;
    }

}




