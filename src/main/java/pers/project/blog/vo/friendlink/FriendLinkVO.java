package pers.project.blog.vo.friendlink;

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
public class FriendLinkVO {

    /**
     * ID
     */
    @Schema(title = "友链 ID")
    private Integer id;

    /**
     * 链接名
     */
    @NotBlank(message = "链接名不能为空")
    @Schema(title = "友链名")
    private String linkName;

    /**
     * 链接头像
     */
    @NotBlank(message = "链接头像不能为空")
    @Schema(title = "友链头像")
    private String linkAvatar;

    /**
     * 链接地址
     */
    @NotBlank(message = "链接地址不能为空")
    @Schema(title = "友链头像")
    private String linkAddress;

    /**
     * 介绍
     */
    @NotBlank(message = "链接介绍不能为空")
    @Schema(title = "友链头像")
    private String linkIntro;

}
