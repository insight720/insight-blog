package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户账号数据
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "用户账号")
public class UserVO {

    /**
     * 用户名
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(name = "username", title = "用户名", type = "String")
    private String username;

    /**
     * 密码
     */
    @Size(min = 6, message = "密码不能少于 6 位")
    @NotBlank(message = "密码不能为空")
    @Schema(name = "password", title = "密码", type = "String")
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Schema(name = "code", title = "邮箱验证码", type = "String")
    private String code;

}
