package pers.project.blog.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 推荐文章
 *
 * @author Luo Fei
 * @version 2023/1/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRecommendDTO {

    /**
     * ID
     */
    private Integer id;

    /**
     * 文章缩略图
     */
    private String articleCover;

    /**
     * 标题
     */
    private String articleTitle;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
