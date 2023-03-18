package pers.project.blog.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.blog.entity.Resource;

import java.util.List;
import java.util.Map;

/**
 * 资源模块数据
 *
 * @author Luo Fei
 * @version 2023/1/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceModuleDTO {

    /**
     * 资源模块列表
     */
    private List<Resource> resourceModuleList;

    /**
     * 模块 ID 与模块下资源的映射
     */
    private Map<Integer, List<Resource>> moduleIdChildrenMap;

}
