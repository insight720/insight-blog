package pers.project.blog.strategy.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.enumeration.SearchModeEnum;
import pers.project.blog.dto.ArticleSearchDTO;
import pers.project.blog.strategy.SearchStrategy;

import java.util.List;
import java.util.Map;

/**
 * 搜索策略上下文
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
@Component
public final class SearchContext {

    @Value("${search.mode}")
    private static String searchMode;
    private static Map<String, SearchStrategy> searchStrategyMap;

    private SearchContext() {
    }

    /**
     * 执行文章搜索策略
     *
     * @param keywords 关键字
     * @return {@code List<ArticleSearchDTO>} 文章数据列表
     */
    public static List<ArticleSearchDTO> executeStrategy(String keywords) {
        return searchStrategyMap.get(SearchModeEnum.getStrategy(searchMode))
                .searchArticle(keywords);
    }

    @Autowired
    public void setSearchStrategyMap(Map<String, SearchStrategy> searchStrategyMap) {
        SearchContext.searchStrategyMap = searchStrategyMap;
    }

}
