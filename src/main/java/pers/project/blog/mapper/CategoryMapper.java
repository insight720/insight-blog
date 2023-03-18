package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.category.AdminCategoryDTO;
import pers.project.blog.dto.category.CategoryDTO;
import pers.project.blog.entity.Category;

import java.util.List;

/**
 * 针对表【tb_category】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @version 2022-12-30
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询分页的后台分类管理数据
     */
    List<AdminCategoryDTO> listAdminCategories(@Param("offset") long offset,
                                               @Param("size") long size,
                                               @Param("keywords") String keywords);

    /**
     * 查询分类 ID、分类名和分类下文章数量
     */
    List<CategoryDTO> listCategories();

}




