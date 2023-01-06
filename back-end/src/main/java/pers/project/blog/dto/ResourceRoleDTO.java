package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 资源角色
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
     * 资源 ID
     */
    private Integer id;

    /**
     * 路径
     */
    private String url;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 角色标签列表
     */
    private List<String> roleList;

}
