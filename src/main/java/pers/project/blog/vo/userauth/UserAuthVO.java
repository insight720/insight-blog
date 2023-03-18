package pers.project.blog.vo.userauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户认证数据
 *
 * @author Luo Fei
 * @version 2023/1/14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthVO {

    /**
     * 用户名（邮箱）
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "用户名（邮箱）")
    private String username;

    /**
     * 密码
     */
    @Size(min = 6, message = "密码不能少于 6 位")
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", type = "String")
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Schema(name = "code", description = "邮箱验证码", type = "String")
    private String code;

}
