package pers.project.blog.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 归档文章
 *
 * @author Luo Fei
 * @version 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleArchiveDTO {

    /**
     * 文章 ID
     */
    private Integer id;

    /**
     * 标题
     */
    private String articleTitle;

    /**
     * 发表时间
     */
    private LocalDateTime createTime;

}
