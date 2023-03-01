package pers.project.blog.dto.talk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论主题 ID 与评论数量数据
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
