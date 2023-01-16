package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 微博登录信息
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "微博登录信息")
public class WeiboLoginVO {

    /**
     * code
     */
    @NotBlank(message = "code不能为空")
    @Schema(name = "openId", description = "qq openId", type = "String")
    private String code;

}
