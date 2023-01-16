package pers.project.blog.strategy;

import pers.project.blog.dto.ArticleSearchDTO;

import java.util.List;

/**
 * @author Luo Fei
 * @date 2023/1/14
 */
public interface SearchStrategy {

    // TODO: 2023/1/14 常量放到常量类
    /**
     * 高亮标签
     */
    String PRE_TAG = "<span style='color:#f47466'>";

    /**
     * 高亮标签
     */
    String POST_TAG = "</span>";

    /**
     * 搜索文章
     *
     * @param keywords 关键字
     * @return {@code  List<ArticleSearchDTO>} 文章数据列表
     */
    List<ArticleSearchDTO> searchArticle(String keywords);

}
