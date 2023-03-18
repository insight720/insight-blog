package pers.project.blog.strategy;

import net.dreamlu.mica.core.exception.ServiceException;
import pers.project.blog.dto.article.ArticleSearchDTO;
import pers.project.blog.util.StrRegexUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 抽象文章搜索策略模板
 *
 * @author Luo Fei
 * @version 2023/2/6
 */
public abstract class AbstractSearchStrategy implements SearchStrategy {

    @Override
    public List<ArticleSearchDTO> searchArticle(String keywords) {
        if (StrRegexUtils.isBlank(keywords)) {
            return new LinkedList<>();
        }
        try {
            return search(keywords);
        } catch (Exception cause) {
            throw new ServiceException("搜索文章失败", cause);
        }
    }

    /**
     * 搜索文章数据
     *
     * @param keywords 关键字
     * @return {@code  List<ArticleSearchDTO>} 文章数据列表
     */
    public abstract List<ArticleSearchDTO> search(String keywords) throws Exception;

}
