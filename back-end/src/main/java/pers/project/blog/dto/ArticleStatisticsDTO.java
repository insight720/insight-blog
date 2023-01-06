package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章统计
 *
 * @author Luo Fei
 * @date 2022/12/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleStatisticsDTO {

    /**
     * 日期
     */
    private String date;

    /**
     * 数量
     */
    private Integer count;

}
