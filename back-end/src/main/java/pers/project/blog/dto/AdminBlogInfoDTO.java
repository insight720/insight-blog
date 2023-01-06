package pers.project.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 博客后台信息
 *
 * @author Luo Fei
 * @date 2022/12/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBlogInfoDTO {

    /**
     * 访问量
     */
    private Integer viewsCount;

    /**
     * 留言量
     */
    private Integer messageCount;

    /**
     * 用户量
     */
    private Integer userCount;

    /**
     * 文章量
     */
    private Integer articleCount;

    /**
     * 分类统计
     */
    @Schema
    private List<CategoryDTO> categoryDTOList;

    /**
     * 标签列表
     */
    @Schema
    private List<TagDTO> tagDTOList;

    /**
     * 文章统计列表
     */
    @Schema
    private List<ArticleStatisticsDTO> articleStatisticsList;

    /**
     * 一周用户量集合
     */
    @Schema
    private List<UniqueViewDTO> uniqueViewDTOList;

    /**
     * 文章浏览量排行
     */
    @Schema
    private List<ArticleRankDTO> articleRankDTOList;

}
