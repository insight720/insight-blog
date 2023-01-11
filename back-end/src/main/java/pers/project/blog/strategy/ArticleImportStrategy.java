package pers.project.blog.strategy;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文章导入策略
 *
 * @author Luo Fei
 * @date 2023/1/7
 */
public interface ArticleImportStrategy {

    /**
     * 导入文章
     *
     * @param multipartFile 文章数据
     */
    void importArticle(MultipartFile multipartFile);

}
