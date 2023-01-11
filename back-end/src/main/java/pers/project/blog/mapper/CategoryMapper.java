package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.AdminCategoryDTO;
import pers.project.blog.dto.CategoryDTO;
import pers.project.blog.entity.CategoryEntity;
import pers.project.blog.vo.ConditionVO;

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

    /**
     * 查询分页的后台分类列表
     *
     * @param offset      条数偏移量
     * @param size        页面最大条数
     * @param conditionVO 条件
     * @return {@code  List<AdminCategoryDTO>} 分页的分类列表
     */
    List<AdminCategoryDTO> listAdminCategoryDTOs(@Param("offset") long offset,
                                                 @Param("size") long size,
                                                 @Param("conditionVO") ConditionVO conditionVO);

}




