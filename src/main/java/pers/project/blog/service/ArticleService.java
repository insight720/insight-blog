package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.dto.article.*;
import pers.project.blog.entity.Article;
import pers.project.blog.vo.article.*;

import java.util.List;

/**
 * 针对表【tb_article】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
public interface ArticleService extends IService<Article> {

    /**
     * 获取分页的后台文章列表数据
     */
    PageDTO<AdminArticleDTO> listAdminArticles(ArticleSearchVO articleSearchVO);

    /**
     * 保存或更新文章
     */
    void saveOrUpdateArticle(ArticleVO articleVO);

    /**
     * 逻辑删除或恢复文章
     */
    void recoverOrRemoveArticlesLogically(TableLogicVO tableLogicVO);

    /**
     * 物理删除文章
     */
    void removeArticlesPhysically(List<Integer> articleIdList);

    /**
     * 导出文章
     *
     * @return 文件 URL 列表
     */
    List<String> exportArticles(List<Integer> articleIdList);

    /**
     * 上传文章图片
     *
     * @return 图片 URL
     */
    String uploadArticleImages(MultipartFile multipartFile);

    /**
     * 导入文章
     */
    void importArticle(MultipartFile multipartFile, String strategyName);

    /**
     * 修改文章置顶状态
     *
     * @param articleTopVO 文章置顶信息
     */
    void updateArticleTop(ArticleTopVO articleTopVO);

    /**
     * 根据 ID 查看后台的文章
     */
    ArticleVO getAdminArticle(Integer articleId);

    /**
     * 查询首页文章数据
     */
    List<HomePageArticleDTO> listHomePageArticles();

    /**
     * 查询文章归档数据
     */
    PageDTO<ArticleArchiveDTO> listArticleArchives();

    /**
     * 根据 ID 查看文章
     */
    ArticleDTO getArticle(Integer articleId);

    /**
     * 获取分类或标签文章预览数据
     */
    ArticlePreviewDTO getArticlePreview(ArticlePreviewVO articlePreviewVO);

    /**
     * 搜索文章
     */
    List<ArticleSearchDTO> listArticlesBySearch(String keywords);

    /**
     * 点赞文章
     */
    void likeArticle(Integer articleId);

}
