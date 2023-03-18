package pers.project.blog.dto.bloginfo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.blog.dto.article.ArticleRankDTO;
import pers.project.blog.dto.article.DailyArticleDTO;
import pers.project.blog.dto.category.CategoryDTO;
import pers.project.blog.dto.tag.TagDTO;

import java.util.List;

/**
 * 博客后台信息
 *
 * @author Luo Fei
 * @version 2022/12/29
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
    private Long messageCount;

    /**
     * 用户量
     */
    private Long userCount;

    /**
     * 文章量
     */
    private Long articleCount;

    /**
     * 分类统计
     */
    private List<CategoryDTO> categoryDTOList;

    /**
     * 标签列表
     */
    private List<TagDTO> tagDTOList;

    /**
     * 文章统计列表
     */
    private List<DailyArticleDTO> articleStatisticsList;

    /**
     * 一周用户量集合
     */
    @JSONField(alternateNames = {"dailyVisitDTOList", "uniqueViewDTOList"})
    private List<DailyVisitDTO> dailyVisitDTOList;

    /**
     * 文章浏览量排行
     */
    private List<ArticleRankDTO> articleRankDTOList;

}
