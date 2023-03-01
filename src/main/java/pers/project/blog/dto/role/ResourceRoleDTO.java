package pers.project.blog.dto.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 接口资源和授权用户角色的信息
 *
 * @author Luo Fei
 * @date 2022/12/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRoleDTO {

    /**
     * 接口资源 ID
     */
    private Integer id;

    /**
     * 接口资源 URI
     */
    private String url;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 匿名可访问（0 否 1 是）
     */
    private Integer isAnonymous;

    /**
     * 授权角色列表
     */
    private List<String> roleList;

}
