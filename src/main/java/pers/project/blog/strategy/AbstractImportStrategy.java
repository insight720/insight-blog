package pers.project.blog.strategy;

import com.aliyun.oss.ServiceException;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.service.ArticleService;
import pers.project.blog.vo.article.ArticleVO;

/**
 * 抽象文章导入策略模板
 *
 * @author Luo Fei
 * @version 2023/2/5
 */
public abstract class AbstractImportStrategy implements ImportStrategy {

    private final ArticleService articleService;

    public AbstractImportStrategy(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void importArticle(MultipartFile multipartFile) {
        ArticleVO articleVO;
        try {
            articleVO = getArticleVO(multipartFile);
        } catch (Exception cause) {
            throw new ServiceException("文章导入失败", cause);
        }
        articleService.saveOrUpdateArticle(articleVO);
    }

    /**
     * 获取文章数据
     *
     * @return {@code ArticleVO} 文章数据
     */
    public abstract ArticleVO getArticleVO(MultipartFile multipartFile) throws Exception;

}
