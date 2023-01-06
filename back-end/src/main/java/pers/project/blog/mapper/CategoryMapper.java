package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.blog.dto.CategoryDTO;
import pers.project.blog.entity.CategoryEntity;

import java.util.List;

/**
 * 针对表【tb_category】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-30
 */
@Mapper
public interface CategoryMapper extends BaseMapper<CategoryEntity> {

    /**
     * 查询分类和对应文章数量
     *
     * @return 分类信息列表
     */
    List<CategoryDTO> listCategoryDTOs();

}




