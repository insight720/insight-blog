package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.category.AdminCategoryDTO;
import pers.project.blog.dto.category.CategoryDTO;
import pers.project.blog.dto.category.CategoryOptionDTO;
import pers.project.blog.service.CategoryService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.category.CategoryVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.REMOVE;
import static pers.project.blog.enums.OperationLogEnum.SAVE_OR_UPDATE;

/**
 * 分类控制器
 *
 * @author Luo Fei
 * @date 2023/1/8
 */
@Tag(name = "分类模块")
@RestController
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "添加或修改分类")
    @PostMapping("/admin/categories")
    public Result<?> saveOrUpdateCategory(@Valid @RequestBody CategoryVO categoryVO) {
        categoryService.saveOrUpdateCategory(categoryVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除分类")
    @DeleteMapping("/admin/categories")
    public Result<?> removeCategories(@NotEmpty @RequestBody List<Integer> categoryIdList) {
        categoryService.removeCategories(categoryIdList);
        return Result.ok();
    }

    @Operation(summary = "查看后台分类列表")
    @GetMapping("/admin/categories")
    public Result<PageDTO<AdminCategoryDTO>> reviewCategoryManagement
            (@RequestParam(required = false) String keywords) {
        return Result.ok(categoryService.listAdminCategories(keywords));
    }

    @Operation(summary = "后台文章列表获取文章分类")
    @GetMapping("/admin/categories/search")
    public Result<List<CategoryOptionDTO>> getAdminArticleCategories() {
        return Result.ok(categoryService.getAdminArticleCategories());
    }

    @Operation(summary = "前台查看分类列表")
    @GetMapping("/categories")
    public Result<PageDTO<CategoryDTO>> listCategories() {
        return Result.ok(categoryService.listCategories());
    }

}
