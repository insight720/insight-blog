package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.AdminCategoryDTO;
import pers.project.blog.dto.CategoryOptionDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.entity.CategoryEntity;
import pers.project.blog.vo.CategoryVO;
import pers.project.blog.vo.ConditionVO;

import java.util.List;

/**
 * 针对表【tb_category】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-30
 */
public interface CategoryService extends IService<CategoryEntity> {

    /**
     * 通过搜索列出分类选项
     *
     * @param condition 条件
     * @return 分类选项列表
     */
    List<CategoryOptionDTO> listCategoriesBySearch(ConditionVO condition);

    /**
     * 查询分页的后台分类列表
     *
     * @param conditionVO 条件
     * @return {@code  PageResult<CategoryBackDTO>} 后台分类的分页数据
     */
    PageDTO<AdminCategoryDTO> listAdminCategories(ConditionVO conditionVO);

    /**
     * 添加或修改分类
     *
     * @param categoryVO 分类
     */
    void saveOrUpdateCategory(CategoryVO categoryVO);

    /**
     * 删除分类
     *
     * @param categoryIdList 分类 ID 集合
     */
    void removeCategories(List<Integer> categoryIdList);

}
