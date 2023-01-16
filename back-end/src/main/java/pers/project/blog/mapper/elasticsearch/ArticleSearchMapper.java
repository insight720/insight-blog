package pers.project.blog.mapper.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import pers.project.blog.dto.ArticleSearchDTO;

/**
 * @author Luo Fei
 * @date 2023/1/16
 */
@Repository
public interface ArticleSearchMapper extends ElasticsearchRepository<ArticleSearchDTO, Integer> {

}