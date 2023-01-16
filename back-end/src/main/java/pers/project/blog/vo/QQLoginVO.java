package pers.project.blog.vo;

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
@Schema(name = "QQ 登录数据")
public class QQLoginVO {

    /**
     * openId
     */
    @NotBlank(message = "openId 不能为空")
    @Schema(name = "openId", title = "QQ openId", type = "String")
    private String openId;

    /**
     * accessToken
     */
    @NotBlank(message = "accessToken 不能为空")
    @Schema(name = "accessToken", title = "QQ accessToken", type = "String")
    private String accessToken;

}
