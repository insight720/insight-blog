package pers.project.blog.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章上下篇
 *
 * @author Luo Fei
 * @date 2023/1/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticlePaginationDTO {

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

}
