package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.*;
import pers.project.blog.entity.ArticleEntity;
import pers.project.blog.vo.ArticleTopVO;
import pers.project.blog.vo.ArticleVO;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.TableLogicVO;

import java.util.List;

/**
 * 针对表【tb_article】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
public interface ArticleService extends IService<ArticleEntity> {

    /**
     * 保存或更新文章
     *
     * @param articleVO 文章数据
     */
    void saveOrUpdateArticle(ArticleVO articleVO);

    /**
     * 查询后台文章
     *
     * @param conditionVO 条件
     * @return 文章列表
     */
    PageDTO<AdminArticleDTO> listAdminArticles(ConditionVO conditionVO);

    /**
     * 逻辑删除或恢复文章
     *
     * @param tableLogicVO 逻辑删除数据
     */
    void recoverOrRemoveArticleLogically(TableLogicVO tableLogicVO);

    /**
     * 修改文章置顶状态
     *
     * @param articleTopVO 文章置顶信息
     */
    void updateArticleTop(ArticleTopVO articleTopVO);

    /**
     * 根据 ID 查看后台的文章
     *
     * @param articleId 文章 ID
     * @return {@code ArticleVO} 后台文章信息
     */
    ArticleVO getAdminArticle(Integer articleId);

    /**
     * 物理删除文章
     *
     * @param articleIdList 文章 ID 列表
     */
    void removeArticlePhysically(List<Integer> articleIdList);

    /**
     * 查询首页文章
     *
     * @return {@code List<HomePageArticleDTO>} 首页文章数据列表
     */
    List<HomePageArticleDTO> listHomePageArticles();

    /**
     * 根据 ID 查看文章
     *
     * @param articleId 文章 ID
     * @return {@code  ArticleDTO} 文章信息
     */
    ArticleDTO getArticle(Integer articleId);

    /**
     * 导出文章
     *
     * @param articleIdList 文章 ID 列表
     * @return {@code  List<String>} 文件 URL 列表
     */
    List<String> exportArticles(List<Integer> articleIdList);

    /**
     * 查询文章归档
     *
     * @return {@code PageDTO<ArticleArchiveDTO>} 分页的文章归档数据
     */
    PageDTO<ArticleArchiveDTO> listArticleArchives();

    /**
     * 根据条件查询文章列表
     *
     * @param conditionVO 条件
     * @return {@code ArticlePreviewDTO} 文章数据列表
     */
    ArticlePreviewDTO getArticlePreview(ConditionVO conditionVO);

    /**
     * 搜索文章
     *
     * @param conditionVO 条件
     * @return {@code ArticleSearchDTO} 文章数据列表
     */
    List<ArticleSearchDTO> listArticlesBySearch(ConditionVO conditionVO);

    /**
     * 点赞文章
     *
     * @param articleId 文章 ID
     */
    void saveArticleLike(Integer articleId);

}
