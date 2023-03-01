package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.article.*;
import pers.project.blog.entity.Article;
import pers.project.blog.vo.article.ArticlePreviewVO;
import pers.project.blog.vo.article.ArticleSearchVO;

import java.util.List;

/**
 * 针对表【tb_article】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 获取每日文章统计数据
     */
    List<DailyArticleDTO> listDailyArticleDTOs();

    /**
     * 查询符合条件的后台文章总量
     */
    Long countAdminArticles(@Param("articleSearchVO") ArticleSearchVO articleSearchVO);

    /**
     * 查询分页的文章列表数据
     */
    List<AdminArticleDTO> listAdminArticles(@Param("offset") long offset,
                                            @Param("size") long size,
                                            @Param("articleSearchVO") ArticleSearchVO articleSearchVO);

    /**
     * 查询首页文章数据
     */
    List<HomePageArticleDTO> listHomePageArticles(@Param("offset") long offset,
                                                  @Param("size") long size);

    /**
     * 查看文章页面的推荐文章
     */
    List<ArticleRecommendDTO> listArticleRecommendArticles(@Param("articleId") Integer articleId);

    /**
     * 根据 ID 查询博客文章信息
     */
    ArticleDTO getArticle(@Param("articleId") Integer articleId);

    /**
     * 根据条件查询文章
     *
     * @param offset 条数偏移量
     * @param size   每页显示条数
     * @return {@code List<ArticlePreviewDTO>}文章数据列表
     */
    List<PreviewDTO> listPreviewDTOs(@Param("offset") long offset,
                                     @Param("size") long size,
                                     @Param("articlePreviewVO") ArticlePreviewVO articlePreviewVO);

}




