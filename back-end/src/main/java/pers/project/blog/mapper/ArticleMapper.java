package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.*;
import pers.project.blog.entity.ArticleEntity;
import pers.project.blog.vo.ConditionVO;

import java.util.List;

/**
 * 针对表【tb_article】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Mapper
public interface ArticleMapper extends BaseMapper<ArticleEntity> {

    /**
     * 文章统计
     *
     * @return 文章统计结果，按日期降序排序
     */
    List<ArticleStatisticsDTO> listArticleStatisticsDTOs();

    /**
     * 查询后台文章总量
     *
     * @param conditionVO 条件
     * @return 文章总量
     */
    Integer countAdminArticles(@Param("conditionVO") ConditionVO conditionVO);

    /**
     * 查询分页的后台文章
     *
     * @param offset      条数偏移量
     * @param size        每页显示条数
     * @param conditionVO 条件
     * @return 分页的文章数据列表
     */
    List<AdminArticleDTO> listAdminArticles(@Param("offset") long offset,
                                            @Param("size") long size,
                                            @Param("conditionVO") ConditionVO conditionVO);

    /**
     * 查询首页文章数据
     *
     * @param offset 条数偏移量
     * @param size   每页显示条数
     * @return {@code List<HomePageArticleDTO>} 首页文章数据列表
     */
    List<HomePageArticleDTO> listHomePageArticleDTOs(@Param("offset") long offset,
                                                     @Param("size") long size);

    /**
     * 查看文章页面的推荐文章
     *
     * @param articleId 文章 ID
     * @return {@code List<ArticleRecommendDTO>} 推荐的文章列表
     */
    List<ArticleRecommendDTO> listArticleRecommendArticles(@Param("articleId") Integer articleId);

    /**
     * 根据 ID 查询博客文章信息
     *
     * @param articleId 文章 ID
     * @return {@code ArticleDTO} 博客文章信息
     */
    ArticleDTO getArticleDTO(@Param("articleId") Integer articleId);

    /**
     * 根据条件查询文章
     *
     * @param offset      条数偏移量
     * @param size        每页显示条数
     * @param conditionVO 条件
     * @return {@code List<ArticlePreviewDTO>}文章数据列表
     */
    List<PreviewDTO> listPreviewDTOs(@Param("offset") long offset,
                                     @Param("size") long size,
                                     @Param("conditionVO") ConditionVO conditionVO);

}




