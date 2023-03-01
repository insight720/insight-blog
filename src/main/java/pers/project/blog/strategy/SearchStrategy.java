package pers.project.blog.strategy;

import pers.project.blog.dto.article.ArticleSearchDTO;

import java.util.List;

/**
 * 文章搜索策略
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
public interface SearchStrategy {

    /**
     * 搜索文章
     *
     * @param keywords 关键字
     * @return {@code  List<ArticleSearchDTO>} 文章数据列表
     */
    List<ArticleSearchDTO> searchArticle(String keywords);

}
