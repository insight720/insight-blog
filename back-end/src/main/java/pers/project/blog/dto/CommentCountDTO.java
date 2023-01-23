package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论 ID 与其数量的映射
 *
 * @author Luo Fei
 * @date 2023/1/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCountDTO {

    /**
     * 评论主题 ID
     */
    private Integer topicId;

    /**
     * 评论数量
     */
    private Integer commentCount;

}
