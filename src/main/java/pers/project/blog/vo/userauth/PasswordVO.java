package pers.project.blog.vo.userauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 后台修改密码数据
 *
 * @author Luo Fei
 * @version 2023/1/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordVO {

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码")
    private String oldPassword;

    /**
     * 新密码
     */
    @Size(min = 6, message = "新密码不能少于 6 位")
    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码")
    private String newPassword;

}
