package pers.project.blog.strategy.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.constant.enumeration.ArticelStatusEnum;
import pers.project.blog.exception.FileUploadException;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.service.ArticleService;
import pers.project.blog.strategy.ArticleImportStrategy;
import pers.project.blog.util.FileIoUtils;
import pers.project.blog.vo.ArticleVO;

import java.nio.charset.StandardCharsets;

/**
 * 普通文章导入策略
 *
 * @author Luo Fei
 * @date 2023/1/7
 */
@Component("normal")
public class NormalArticleImportStrategyImpl implements ArticleImportStrategy {

    private final ArticleService articleService;

    public NormalArticleImportStrategyImpl(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void importArticle(MultipartFile multipartFile) {
        // 获取文件名生成文章标题
        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new ServiceException("文章名不能为空");
        }
        String articleTitle = FileIoUtils.getMainName(originalFilename);

        // 读取文章内容
        String articleContent;
        try {
            byte[] bytes = FileIoUtils.readBytes(multipartFile.getInputStream());
            articleContent = new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception cause) {
            throw new FileUploadException("文章导入失败", cause);
        }

        // 保存文章数据，默认保存为无封面和分类的草稿
        ArticleVO articleVO = ArticleVO.builder()
                .articleTitle(articleTitle)
                .articleContent(articleContent)
                .status(ArticelStatusEnum.DRAFT.getStatus())
                .build();
        articleService.saveOrUpdateArticle(articleVO);
    }

}
