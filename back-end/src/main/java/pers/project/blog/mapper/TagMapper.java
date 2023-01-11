package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.AdminTagDTO;
import pers.project.blog.entity.TagEntity;
import pers.project.blog.vo.ConditionVO;

import java.util.List;

/**
 * 针对表【tb_tag】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-30
 */
@Mapper
public interface TagMapper extends BaseMapper<TagEntity> {

    /**
     * 根据文章 ID 查询标签名
     *
     * @param articleId 文章 ID
     * @return {@code List<String>} 标签名列表
     */
    List<String> listTagNamesByArticleId(@Param("articleId") Integer articleId);

    /**
     * 查询后台的分页标签列表
     *
     * @param offset      条数偏移量
     * @param size        页面最大条数
     * @param conditionVO 条件
     * @return {@code  List<AdminTagDTO>} 分页标签列表
     */
    List<AdminTagDTO> listAdminTags(@Param("offset") long offset,
                                    @Param("size") long size,
                                    @Param("conditionVO") ConditionVO conditionVO);

}




