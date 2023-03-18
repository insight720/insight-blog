package pers.project.blog.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论回复数据
 *
 * @author Luo Fei
 * @version 2023/1/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {

    /**
     * 评论 ID
     */
    private Integer id;

    /**
     * 父评论 ID
     */
    private Integer parentId;

    /**
     * 用户 ID
     */
    private Integer userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 个人网站
     */
    private String webSite;

    /**
     * 被回复用户 ID
     */
    private Integer replyUserId;

    /**
     * 被回复用户昵称
     */
    private String replyNickname;

    /**
     * 被回复个人网站
     */
    private String replyWebSite;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论时间
     */
    private LocalDateTime createTime;

}
