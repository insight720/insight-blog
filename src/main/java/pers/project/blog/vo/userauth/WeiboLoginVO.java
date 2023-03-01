package pers.project.blog.vo.userauth;

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
public class WeiboLoginVO {

    /**
     * code
     */
    @NotBlank(message = "code 不能为空")
    @Schema(description = "微博 code")
    private String code;

}
