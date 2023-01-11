package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.AdminArticleDTO;
import pers.project.blog.dto.ArticleDTO;
import pers.project.blog.dto.PageDTO;
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
     * 根据id查看文章
     *
     * @param articleId 文章id
     * @return 文章信息
     */
    ArticleDTO getAdminArticleById(Integer articleId);

    /**
     * 物理删除文章
     *
     * @param articleIdList 文章 ID 集合
     */
    void removeArticlePhysically(List<Integer> articleIdList);

}
