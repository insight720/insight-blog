package pers.project.blog.listener;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import pers.project.blog.dto.article.ArticleSearchDTO;

/**
 * Elasticsearch 文章数据的操作接口
 *
 * @author Luo Fei
 * @date 2023/1/16
 */
@Repository
public interface ArticleRepository extends ElasticsearchRepository<ArticleSearchDTO, Integer> {
}
