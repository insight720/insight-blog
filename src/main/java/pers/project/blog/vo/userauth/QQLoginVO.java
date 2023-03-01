package pers.project.blog.vo.userauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * QQ 登录数据
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QQLoginVO {

    /**
     * openId
     */
    @NotBlank(message = "openId 不能为空")
    @Schema(description = "QQ openId")
    private String openId;

    /**
     * accessToken
     */
    @NotBlank(message = "accessToken 不能为空")
    @Schema(description = "QQ accessToken")
    private String accessToken;

}
