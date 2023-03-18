package pers.project.blog.strategy.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.article.ArticleSearchDTO;
import pers.project.blog.property.SearchProperties;
import pers.project.blog.strategy.SearchStrategy;

import java.util.List;

/**
 * 搜索策略上下文
 *
 * @author Luo Fei
 * @version 2023/1/14
 */
@Component
@EnableConfigurationProperties(SearchProperties.class)
public final class SearchContext {

    private static SearchStrategy searchStrategy;

    @Autowired
    public SearchContext(SearchStrategy searchStrategy) {
        SearchContext.searchStrategy = searchStrategy;
    }

    /**
     * 执行文章搜索策略
     *
     * @param keywords 关键字
     * @return {@code List<ArticleSearchDTO>} 文章数据列表
     */
    public static List<ArticleSearchDTO> executeStrategy(String keywords) {
        return searchStrategy.searchArticle(keywords);
    }

}
