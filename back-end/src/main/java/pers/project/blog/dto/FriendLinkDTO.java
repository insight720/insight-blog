package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端友链
 *
 * @author Luo Fei
 * @date 2023/1/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendLinkDTO {

    /**
     * 链接 ID
     */
    private Integer id;

    /**
     * 链接名
     */
    private String linkName;

    /**
     * 链接头像
     */
    private String linkAvatar;

    /**
     * 链接地址
     */
    private String linkAddress;

    /**
     * 介绍
     */
    private String linkIntro;

}
