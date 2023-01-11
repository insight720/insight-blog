package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 密码信息
 *
 * @author Luo Fei
 * @date 2023/1/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "密码信息")
public class PasswordVO {

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    @Schema(name = "oldPassword", title = "旧密码", type = "String")
    private String oldPassword;

    /**
     * 新密码
     */
    @Size(min = 6, message = "新密码不能少于 6 位")
    @NotBlank(message = "新密码不能为空")
    @Schema(name = "newPassword", title = "新密码", type = "String")
    private String newPassword;

}
