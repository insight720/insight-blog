package pers.project.blog.dto.friendlink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 后台友链数据
 *
 * @author Luo Fei
 * @version 2023/1/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminFriendLinkDTO {

    /**
     * ID
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
     * 链接介绍
     */
    private String linkIntro;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
