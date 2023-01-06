package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.blog.dto.ArticleStatisticsDTO;
import pers.project.blog.entity.ArticleEntity;

import java.util.List;

/**
 * 针对表【tb_article】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Mapper
public interface ArticleMapper extends BaseMapper<ArticleEntity> {

    /**
     * 文章统计
     *
     * @return 文章统计结果，按日期降序排序
     */
    List<ArticleStatisticsDTO> listArticleStatisticsDTOs();

}




