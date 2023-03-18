package pers.project.blog.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 后台留言数据
 *
 * @author Luo Fei
 * @version 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMessageDTO {

    /**
     * 主键 ID
     */
    private Integer id;

    /**
     * 用户 IP
     */
    private String ipAddress;

    /**
     * 用户 IP 地址
     */
    private String ipSource;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 留言内容
     */
    private String messageContent;

    /**
     * 是否审核
     */
    private Integer isReview;

    /**
     * 留言时间
     */
    private LocalDateTime createTime;

}
