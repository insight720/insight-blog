package pers.project.blog.strategy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.enums.ArticelStateEnum;
import pers.project.blog.service.ArticleService;
import pers.project.blog.strategy.AbstractImportStrategy;
import pers.project.blog.util.FileIoUtils;
import pers.project.blog.util.StrRegexUtils;
import pers.project.blog.vo.article.ArticleVO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static pers.project.blog.constant.ImportConst.NORMAL;

/**
 * 普通文章导入策略
 *
 * @author Luo Fei
 * @version 2023/1/7
 */
@Component(NORMAL)
public class NormalImportStrategyImpl extends AbstractImportStrategy {

    @Autowired
    public NormalImportStrategyImpl(ArticleService articleService) {
        super(articleService);
    }

    @Override
    public ArticleVO getArticleVO(MultipartFile multipartFile) throws IOException {
        // 获取文件名生成文章标题
        String originalFilename = multipartFile.getOriginalFilename();
        if (StrRegexUtils.isBlank(originalFilename)) {
            throw new RuntimeException("无法获取文件名");
        }
        String articleTitle = FileIoUtils.getMainName(originalFilename);
        // 读取文章内容
        String articleContent;
        byte[] bytes = FileIoUtils.readBytes(multipartFile.getInputStream());
        articleContent = new String(bytes, StandardCharsets.UTF_8);
        // 保存文章数据，默认保存为无封面和分类的草稿
        return ArticleVO.builder()
                .articleTitle(articleTitle)
                .articleContent(articleContent)
                .status(ArticelStateEnum.DRAFT.getStatus())
                .build();
    }

}
