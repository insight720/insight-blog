package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.category.AdminCategoryDTO;
import pers.project.blog.dto.category.CategoryDTO;
import pers.project.blog.dto.category.CategoryOptionDTO;
import pers.project.blog.entity.Article;
import pers.project.blog.entity.Category;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.ArticleMapper;
import pers.project.blog.mapper.CategoryMapper;
import pers.project.blog.service.CategoryService;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.util.PageUtils;
import pers.project.blog.util.StrRegexUtils;
import pers.project.blog.vo.category.CategoryVO;

import javax.annotation.Resource;
import java.util.List;

import static pers.project.blog.constant.GenericConst.ZERO_L;

/**
 * 针对表【tb_category】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-30
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public void saveOrUpdateCategory(CategoryVO categoryVO) {
        // 新建分类时，查询分类名是否已存在
        boolean save = categoryVO.getId() == null;
        if (save) {
            boolean exists = lambdaQuery()
                    .eq(Category::getCategoryName, categoryVO.getCategoryName())
                    .exists();
            if (exists) {
                throw new ServiceException("分类名已存在");
            }
        }
        Category category = Category.builder()
                .id(categoryVO.getId())
                .categoryName(categoryVO.getCategoryName())
                .build();
        saveOrUpdate(category);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void removeCategories(List<Integer> categoryIdList) {
        // 判断分类下是否有文章
        boolean exists = new LambdaQueryChainWrapper<>(articleMapper)
                .in(Article::getCategoryId, categoryIdList)
                .exists();
        if (exists) {
            throw new ServiceException("删除失败，该分类下存在文章");
        }
        removeBatchByIds(categoryIdList);
    }

    @Override
    public PageDTO<AdminCategoryDTO> listAdminCategories(String keywords) {
        // 查询符合条件的分类总数
        Long adminCategoryCount = lambdaQuery()
                .like(StrRegexUtils.isNotBlank(keywords),
                        Category::getCategoryName, keywords)
                .count();
        if (adminCategoryCount.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        // 查询符合条件的分页分类数据
        List<AdminCategoryDTO> adminCategoryList = baseMapper.listAdminCategories
                (PageUtils.offset(), PageUtils.size(), keywords);
        return PageUtils.build(adminCategoryList, adminCategoryCount);
    }

    @Override
    public List<CategoryOptionDTO> getAdminArticleCategories() {
        List<Category> categoryOptionList = lambdaQuery()
                .select(Category::getId, Category::getCategoryName)
                .orderByDesc(Category::getId)
                .list();
        return ConvertUtils.convertList(categoryOptionList, CategoryOptionDTO.class);
    }

    @Override
    public PageDTO<CategoryDTO> listCategories() {
        return PageUtils.build(baseMapper.listCategories(), count());
    }

}




