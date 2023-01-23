package pers.project.blog.strategy.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.BooleanConstant;
import pers.project.blog.constant.enumeration.ArticleStatusEnum;
import pers.project.blog.dto.ArticleSearchDTO;
import pers.project.blog.strategy.SearchStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ElasticSearch 搜索策略
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
@Component
public class ElasticSearchStrategyImpl implements SearchStrategy {

    private final ElasticsearchRestTemplate restTemplate;

    public ElasticSearchStrategyImpl(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ArticleSearchDTO> searchArticle(String keywords) {
        // TODO: 2023/1/14 判空可以导到策略外面
        return Optional.ofNullable(keywords)
                .filter(StringUtils::isNotBlank)
                // keywords 不为空，获取搜索结果并使标题和内容的高亮
                .map(ignored -> restTemplate
                        .search(getSearchQuery(keywords), ArticleSearchDTO.class)
                        .stream()
                        .map(searchHit -> {
                            ArticleSearchDTO articleSearchDTO = searchHit.getContent();
                            Map<String, List<String>> highlightFields
                                    = searchHit.getHighlightFields();
                            List<String> highLightTitle
                                    = highlightFields.get("articleTitle");
                            if (CollectionUtils.isNotEmpty(highLightTitle)) {
                                articleSearchDTO.setArticleTitle(highLightTitle.get(0));
                            }
                            List<String> highLightContent
                                    = highlightFields.get("articleContent");
                            // TODO: 2023/1/17 为什么是最后一个 ？
                            if (CollectionUtils.isNotEmpty(highLightContent)) {
                                articleSearchDTO.setArticleContent(highLightContent.get(highLightContent.size() - 1));
                            }
                            return articleSearchDTO;
                        })
                        .collect(Collectors.toList()))
                // keywords 为空
                .orElseGet(ArrayList::new);
    }
// TODO: 2023/1/17 ES 实现有异常

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
                        .should(QueryBuilders.matchQuery("articleTitle", keywords))
                        .should(QueryBuilders.matchQuery("articleContent", keywords)))
                .must(QueryBuilders.termQuery("isDelete", BooleanConstant.FALSE))
                .must(QueryBuilders.termQuery("status", ArticleStatusEnum.PUBLIC.getStatus()));
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        // TODO: 2023/1/14 封装魔法值
        // 添加文章标题和文章内容高亮
        HighlightBuilder.Field articleTitle = new HighlightBuilder.Field("articleTitle");
        articleTitle.preTags(PRE_TAG);
        articleTitle.postTags(POST_TAG);
        HighlightBuilder.Field articleContent = new HighlightBuilder.Field("articleContent");
        articleContent.preTags(PRE_TAG);
        articleContent.postTags(POST_TAG);
        articleContent.fragmentSize(200);
        nativeSearchQueryBuilder.withHighlightFields(articleTitle, articleContent);

        return nativeSearchQueryBuilder.build();
    }

}
