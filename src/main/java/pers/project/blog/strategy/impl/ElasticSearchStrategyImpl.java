package pers.project.blog.strategy.impl;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.article.ArticleSearchDTO;
import pers.project.blog.strategy.AbstractSearchStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pers.project.blog.constant.GenericConst.*;
import static pers.project.blog.constant.SearchConst.*;
import static pers.project.blog.enums.ArticelStateEnum.PUBLIC;

/**
 * ElasticSearch 搜索策略
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
@Component
@ConditionalOnProperty(prefix = "blog.search", name = "strategy", havingValue = "elasticsearch")
public class ElasticSearchStrategyImpl extends AbstractSearchStrategy {

    private final ElasticsearchRestTemplate restTemplate;

    public ElasticSearchStrategyImpl(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ArticleSearchDTO> search(String keywords) {
        return restTemplate
                .search(getSearchQuery(keywords), ArticleSearchDTO.class)
                .stream()
                .map(searchHit -> {
                    // 设置文章标题和文章内容高亮
                    ArticleSearchDTO articleSearchDTO = searchHit.getContent();
                    Map<String, List<String>> highlightFields
                            = searchHit.getHighlightFields();
                    List<String> highLightTitle
                            = highlightFields.get(ARTICLE_TITLE);
                    if (CollectionUtils.isNotEmpty(highLightTitle)) {
                        articleSearchDTO.setArticleTitle(highLightTitle.get(ZERO));
                    }
                    List<String> highLightContent
                            = highlightFields.get(ARTICLE_CONTENT);
                    if (CollectionUtils.isNotEmpty(highLightContent)) {
                        articleSearchDTO.setArticleContent(highLightContent.get(ZERO));
                    }
                    return articleSearchDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取搜索查询
     *
     * @param keywords 关键词
     * @return {@code NativeSearchQuery} 搜索查询
     */
    private NativeSearchQuery getSearchQuery(String keywords) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        // 组合查询条件构造器，根据关键词搜索文章标题或内容
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery(ARTICLE_TITLE, keywords))
                        .should(QueryBuilders.matchQuery(ARTICLE_CONTENT, keywords)))
                .must(QueryBuilders.termQuery(IS_DELETE, FALSE_OF_INT))
                .must(QueryBuilders.termQuery(STATUS, PUBLIC.getStatus()));
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        // 添加文章标题和文章内容高亮
        HighlightBuilder.Field articleTitle = new HighlightBuilder.Field(ARTICLE_TITLE);
        articleTitle.preTags(PRE_TAG);
        articleTitle.postTags(POST_TAG);
        HighlightBuilder.Field articleContent = new HighlightBuilder.Field(ARTICLE_CONTENT);
        articleContent.preTags(PRE_TAG);
        articleContent.postTags(POST_TAG);
        // 高亮文章内容最长 200 字符
        articleContent.fragmentSize(TWO_HUNDRED);
        nativeSearchQueryBuilder.withHighlightFields(articleTitle, articleContent);
        return nativeSearchQueryBuilder.build();
    }

}
