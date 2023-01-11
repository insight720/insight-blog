package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 用户信息
 *
 * @author Luo Fei
 * @date 2023/1/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "用户信息")
public class UserInfoVO {

    /**
     * 用户昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Schema(name = "nickname", title = "昵称", type = "String")
    private String nickname;

    /**
     * 用户简介
     */
    @Schema(name = "intro", title = "介绍", type = "String")
    private String intro;

    /**
     * 个人网站
     */
    @Schema(name = "webSite", title = "个人网站", type = "String")
    private String webSite;

}
