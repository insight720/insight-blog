package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.AdminCategoryDTO;
import pers.project.blog.dto.CategoryOptionDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.service.CategoryService;
import pers.project.blog.vo.CategoryVO;
import pers.project.blog.vo.ConditionVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 分类控制器
 *
 * @author Luo Fei
 * @date 2023/1/8
 */
@Tag(name = "分类模块")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "搜索文章分类")
    @GetMapping("/admin/categories/search")
    public Result<List<CategoryOptionDTO>> listCategoriesBySearch(ConditionVO conditionVO) {
        return Result.ok(categoryService.listCategoriesBySearch(conditionVO));
    }

    @Operation(summary = "查看后台分类列表")
    @GetMapping("/admin/categories")
    public Result<PageDTO<AdminCategoryDTO>> listAdminCategories(ConditionVO conditionVO) {
        return Result.ok(categoryService.listAdminCategories(conditionVO));
    }

    @OperationLog(type = LogConstant.SAVE_OR_UPDATE)
    @Operation(summary = "添加或修改分类")
    @PostMapping("/admin/categories")
    public Result<?> saveOrUpdateCategory(@Valid @RequestBody CategoryVO categoryVO) {
        categoryService.saveOrUpdateCategory(categoryVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.REMOVE)
    @Operation(summary = "删除分类")
    @DeleteMapping("/admin/categories")
    public Result<?> removeCategories(@RequestBody List<Integer> categoryIdList) {
        categoryService.removeCategories(categoryIdList);
        return Result.ok();
    }

}
