package pers.project.blog.strategy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.article.ArticleSearchDTO;
import pers.project.blog.entity.Article;
import pers.project.blog.service.ArticleService;
import pers.project.blog.strategy.AbstractSearchStrategy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static pers.project.blog.constant.GenericConst.*;
import static pers.project.blog.constant.SearchConst.POST_TAG;
import static pers.project.blog.constant.SearchConst.PRE_TAG;
import static pers.project.blog.enums.ArticelStateEnum.PUBLIC;

/**
 * MySql 搜索策略
 *
 * @author Luo Fei
 * @version 2023/1/14
 */
@Component
@ConditionalOnProperty(prefix = "blog.search.strategy", name = "strategy", havingValue = "mysql")
public class MySqlSearchStrategyImpl extends AbstractSearchStrategy {

    private final ArticleService articleService;

    @Autowired
    public MySqlSearchStrategyImpl(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public List<ArticleSearchDTO> search(String keywords) {
        // 按条件查询文章数据
        return articleService.lambdaQuery()
                .eq(Article::getIsDelete, FALSE_OF_INT)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .and(lambdaQuery -> lambdaQuery
                        .like(Article::getArticleTitle, keywords)
                        .or().like(Article::getArticleContent, keywords))
                .list().stream()
                // 处理文章数据
                .map(article -> processArticle(article, keywords))
                .collect(Collectors.toList());
    }

    /**
     * 处理文章数据
     */
    private ArticleSearchDTO processArticle(Article article, String keywords) {
        // 高亮处理的关键词
        String replacement = PRE_TAG + keywords + POST_TAG;
        // 高亮处理标题
        String articleTitle = article.getArticleTitle()
                .replaceAll(keywords, replacement);
        // 高亮处理内容
        AtomicReference<String> contentReference
                = new AtomicReference<>(article.getArticleContent());
        String articleContent = Optional
                .of(contentReference.get().indexOf(keywords))
                .filter(keyIndex -> keyIndex != -ONE)
                .map(keyStartIndex -> {
                    // 获取关键词前面的文字，最多 25 个
                    int previousIndex = keyStartIndex > TWENTY_FIVE ?
                            keyStartIndex - TWENTY_FIVE : ZERO;
                    String previousText = contentReference.get()
                            .substring(previousIndex, keyStartIndex);
                    // 获取关键词和其后面的文字，最多 175 个
                    int keyEndIndex = keyStartIndex + keywords.length();
                    int latterLength = contentReference.get()
                            .length() - keyEndIndex;
                    int latterIndex = latterLength > O_H_A_SEVENTY_FIVE ?
                            keyEndIndex + O_H_A_SEVENTY_FIVE : keyEndIndex + latterLength;
                    String latterText = contentReference.get()
                            .substring(keyStartIndex, latterIndex);
                    contentReference.set((previousText + latterText)
                            .replaceAll(keywords, replacement));
                    return contentReference.get();
                })
                .orElseGet(article::getArticleContent);
        return ArticleSearchDTO.builder()
                .id(article.getId())
                .articleTitle(articleTitle)
                .articleContent(articleContent)
                .build();
    }

}


