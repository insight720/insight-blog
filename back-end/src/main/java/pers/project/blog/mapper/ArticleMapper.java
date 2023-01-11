package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.AdminArticleDTO;
import pers.project.blog.dto.ArticleStatisticsDTO;
import pers.project.blog.entity.ArticleEntity;
import pers.project.blog.vo.ConditionVO;

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

    /**
     * 查询后台文章总量
     *
     * @param conditionVO 条件
     * @return 文章总量
     */
    Integer countAdminArticles(@Param("condition") ConditionVO condition);

    /**
     * 查询分页的后台文章
     *
     * @param offset      条数偏移量
     * @param size        每页显示条数
     * @param conditionVO 条件
     * @return 分页的文章数据列表
     */
    List<AdminArticleDTO> listAdminArticles(@Param("offset") long offset,
                                            @Param("size") long size,
                                            @Param("condition") ConditionVO condition);
}




