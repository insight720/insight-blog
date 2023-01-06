package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.blog.entity.ArticleTagEntity;

/**
 * 针对表【tb_article_tag】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023-01-05
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTagEntity> {

}




