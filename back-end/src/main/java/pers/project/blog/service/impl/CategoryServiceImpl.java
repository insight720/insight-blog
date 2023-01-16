package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pers.project.blog.dto.AdminCategoryDTO;
import pers.project.blog.dto.CategoryDTO;
import pers.project.blog.dto.CategoryOptionDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.entity.ArticleEntity;
import pers.project.blog.entity.CategoryEntity;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.ArticleMapper;
import pers.project.blog.mapper.CategoryMapper;
import pers.project.blog.service.CategoryService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.vo.CategoryVO;
import pers.project.blog.vo.ConditionVO;

import java.util.List;

/**
 * 针对表【tb_category】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-30
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    private final ArticleMapper articleMapper;

    public CategoryServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public List<CategoryOptionDTO> listCategoriesBySearch(ConditionVO condition) {
        List<CategoryEntity> categoryOptionList = lambdaQuery()
                .like(StringUtils.isNotBlank(condition.getKeywords()),
                        CategoryEntity::getCategoryName, condition.getKeywords())
                .orderByDesc(CategoryEntity::getId)
                .list();
        return ConversionUtils.covertList(categoryOptionList, CategoryOptionDTO.class);
    }

    @Override
    public PageDTO<AdminCategoryDTO> listAdminCategories(ConditionVO conditionVO) {
        // 查询符合条件的分类总数
        String keywords = conditionVO.getKeywords();
        Long adminCategoryCount = lambdaQuery()
                .like(StringUtils.isNotBlank(keywords),
                        CategoryEntity::getCategoryName, keywords)
                .count();
        if (adminCategoryCount == 0) {
            return new PageDTO<>();
        }

        // 查询符合条件的分页分类列表
        IPage<CategoryEntity> page = PaginationUtils.getPage();
        List<AdminCategoryDTO> adminCategoryList = baseMapper.listAdminCategoryDTOs
                (page.offset(), page.getSize(), conditionVO);

        return PageDTO.of(adminCategoryList, adminCategoryCount.intValue());
    }

    @Override
    public void saveOrUpdateCategory(CategoryVO categoryVO) {
        // 判断分类是否已存在
        CategoryEntity categoryEntity = lambdaQuery()
                .select(CategoryEntity::getId)
                .eq(CategoryEntity::getCategoryName, categoryVO.getCategoryName())
                .one();
        if (categoryEntity != null && categoryEntity.getId().equals(categoryVO.getId())) {
            throw new ServiceException("分类已经存在");
        }

        CategoryEntity newCategoryEntity = CategoryEntity.builder()
                .id(categoryVO.getId())
                .categoryName(categoryVO.getCategoryName())
                .build();
        saveOrUpdate(newCategoryEntity);
    }

    @Override
    public void removeCategories(List<Integer> categoryIdList) {
        // 判断分类下是否有文章
        Long articleCount = new LambdaQueryChainWrapper<>(articleMapper)
                .in(ArticleEntity::getCategoryId, categoryIdList)
                .count();
        if (articleCount > 0) {
            throw new ServiceException("删除失败，该分类下存在文章");
        }

        removeBatchByIds(categoryIdList);
    }

    @Override
    public PageDTO<CategoryDTO> listCategories() {
        return PageDTO.of(baseMapper.listCategoryDTOs(),
                baseMapper.selectCount(null).intValue());
    }

}




