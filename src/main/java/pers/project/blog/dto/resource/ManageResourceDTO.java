package pers.project.blog.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源管理数据
 *
 * @author Luo Fei
 * @version 2023/1/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManageResourceDTO {

    /**
     * 资源 ID
     */
    private Integer id;

    /**
     * 资源名
     */
    private String resourceName;

    /**
     * 权限 URI
     */
    private String url;

    /**
     * 请求方式
     */
    private String requestMethod;
    // unused
    /**
     * 是否禁用
     */
    private Integer isDisable;

    /**
     * 是否匿名访问
     */
    private Integer isAnonymous;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子资源列表
     */
    private List<ManageResourceDTO> children;

}
