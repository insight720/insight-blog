package pers.project.blog.strategy.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.BooleanConstant;
import pers.project.blog.constant.enumeration.ArticleStatusEnum;
import pers.project.blog.dto.ArticleSearchDTO;
import pers.project.blog.entity.ArticleEntity;
import pers.project.blog.service.ArticleService;
import pers.project.blog.strategy.SearchStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * MySql 搜索策略
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
@Component
public class MySqlSearchStrategyImpl implements SearchStrategy {

    private final ArticleService articleService;

    public MySqlSearchStrategyImpl(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public List<ArticleSearchDTO> searchArticle(String keywords) {
        return Optional.ofNullable(keywords)
                .filter(StringUtils::isNotBlank)
                // keywords 不为空，查询符合搜索条件的数据
                .map(ignored -> articleService.lambdaQuery()
                        .eq(ArticleEntity::getIsDelete, BooleanConstant.FALSE)
                        .eq(ArticleEntity::getStatus, ArticleStatusEnum.PUBLIC.getStatus())
                        .and(lambdaQuery -> lambdaQuery
                                .like(ArticleEntity::getArticleTitle, keywords)
                                .or().like(ArticleEntity::getArticleContent, keywords))
                        .list().stream()
                        .map(article -> {
                            // 高亮处理标题
                            String replacement = PRE_TAG + keywords + POST_TAG;
                            String articleTitle = article.getArticleTitle()
                                    .replaceAll(keywords, replacement);

                            // 高亮处理内容
                            AtomicReference<String> contentReference
                                    = new AtomicReference<>(article.getArticleContent());
                            String articleContent = Optional
                                    .of(contentReference.get().indexOf(keywords))
                                    .filter(keyIndex -> keyIndex != -1)
                                    .map(keyStartIndex -> {
                                        // 获取关键词前面的文字，最多 25 个
                                        int previousIndex = keyStartIndex > 25 ?
                                                keyStartIndex - 25 : 0;
                                        String previousText = contentReference.get()
                                                .substring(previousIndex, keyStartIndex);

                                        // 获取关键词和其后面的文字，最多 175 个
                                        int keyEndIndex = keyStartIndex + keywords.length();
                                        int latterLength = contentReference.get()
                                                .length() - keyEndIndex;
                                        int latterIndex = latterLength > 175 ?
                                                keyEndIndex + 175 : keyEndIndex + latterLength;
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
                        })
                        .collect(Collectors.toList()))
                // keywords 为空
                .orElseGet(ArrayList::new);
    }

}
