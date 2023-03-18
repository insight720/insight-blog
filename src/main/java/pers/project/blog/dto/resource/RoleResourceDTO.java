package pers.project.blog.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 角色资源数据
 *
 * @author Luo Fei
 * @version 2023/1/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResourceDTO {

    /**
     * 资源 ID
     */
    private Integer id;

    /**
     * 资源名
     */
    private String label;

    /**
     * 子资源
     */
    private List<RoleResourceDTO> children;

}
