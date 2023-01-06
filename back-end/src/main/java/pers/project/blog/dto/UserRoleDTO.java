package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户角色选项
 *
 * @author Luo Fei
 * @date 2022/12/31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDTO {

    /**
     * 角色 ID
     */
    private Integer id;

    /**
     * 角色名
     */
    private String roleName;

}
