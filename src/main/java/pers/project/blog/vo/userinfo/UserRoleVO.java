package pers.project.blog.vo.userinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户昵称和角色数据
 *
 * @author Luo Fei
 * @date 2023/1/1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleVO {

    /**
     * 用户 ID
     */
    @NotNull(message = "ID 不能为空")
    @Schema(name = "userInfoId", description = "用户信息id", type = "Integer")
    private Integer userInfoId;

    /**
     * 用户昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Schema(name = "nickname", description = "昵称", type = "String")
    private String nickname;

    /**
     * 用户角色
     */
    @NotNull(message = "用户角色不能为空")
    @Schema(name = "roleList", description = "角色id集合", type = "List<Integer>")
    private List<Integer> roleIdList;

}
