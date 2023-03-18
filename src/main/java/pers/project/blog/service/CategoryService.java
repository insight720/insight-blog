package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.category.AdminCategoryDTO;
import pers.project.blog.dto.category.CategoryDTO;
import pers.project.blog.dto.category.CategoryOptionDTO;
import pers.project.blog.entity.Category;
import pers.project.blog.vo.category.CategoryVO;

import java.util.List;

/**
 * 针对表【tb_category】的数据库操作 Service
 *
 * @author Luo Fei
 * @version 2022-12-30
 */
public interface CategoryService extends IService<Category> {

    /**
     * 添加或修改分类
     */
    void saveOrUpdateCategory(CategoryVO categoryVO);

    /**
     * 删除分类
     */
    void removeCategories(List<Integer> categoryIdList);

    /**
     * 查询分页的后台分类管理数据
     */
    PageDTO<AdminCategoryDTO> listAdminCategories(String keywords);

    /**
     * 获取后台文章列表所需文章分类数据
     */
    List<CategoryOptionDTO> getAdminArticleCategories();

    /**
     * 查询分页的分类列表
     */
    PageDTO<CategoryDTO> listCategories();

}
