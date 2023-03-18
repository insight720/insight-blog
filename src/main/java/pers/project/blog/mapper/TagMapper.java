package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.tag.ManageTagDTO;
import pers.project.blog.entity.Tag;

import java.util.List;

/**
 * 针对表【tb_tag】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @version 2022-12-30
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据文章 ID 查询标签名
     */
    List<String> listArticleTagNames(@Param("articleId") Integer articleId);

    /**
     * 查询分页的后台标签管理数据
     */
    List<ManageTagDTO> listAdminTags(@Param("offset") long offset,
                                     @Param("size") long size,
                                     @Param("keywords") String keywords);

}




