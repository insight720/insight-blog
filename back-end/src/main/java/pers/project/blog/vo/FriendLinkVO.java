package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 友链信息
 *
 * @author Luo Fei
 * @date 2023/1/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "友链信息")
public class FriendLinkVO {

    /**
     * ID
     */
    @Schema(name = "id", title = "友链 ID", type = "Integer")
    private Integer id;

    /**
     * 链接名
     */
    @NotBlank(message = "链接名不能为空")
    @Schema(name = "linkName", title = "友链名", type = "String")
    private String linkName;

    /**
     * 链接头像
     */
    @NotBlank(message = "链接头像不能为空")
    @Schema(name = "linkAvatar", title = "友链头像", type = "String")
    private String linkAvatar;

    /**
     * 链接地址
     */
    @NotBlank(message = "链接地址不能为空")
    @Schema(name = "linkAddress", title = "友链头像", type = "String")
    private String linkAddress;

    /**
     * 介绍
     */
    @NotBlank(message = "链接介绍不能为空")
    @Schema(name = "linkIntro", title = "友链头像", type = "String")
    private String linkIntro;

}
