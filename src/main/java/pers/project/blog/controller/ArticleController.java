package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.article.*;
import pers.project.blog.service.ArticleService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.article.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.*;

/**
 * 文章控制器
 *
 * @author Luo Fei
 * @version 2023/1/8
 */
@Tag(name = "文章模块")
@Validated
@RestController
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Operation(summary = "查看后台文章列表")
    @GetMapping("/admin/articles")
    public Result<PageDTO<AdminArticleDTO>> viewAdminArticles(@Valid ArticleSearchVO articleSearchVO) {
        return Result.ok(articleService.listAdminArticles(articleSearchVO));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "添加或修改文章")
    @PostMapping("/admin/articles")
    public Result<?> saveOrUpdateArticle(@Valid @RequestBody ArticleVO articleVO) {
        articleService.saveOrUpdateArticle(articleVO);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "恢复或逻辑删除文章")
    @PutMapping("/admin/articles")
    public Result<?> recoverOrRemoveArticleLogically(@Valid @RequestBody TableLogicVO tableLogicVO) {
        articleService.recoverOrRemoveArticlesLogically(tableLogicVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "物理删除文章")
    @DeleteMapping("/admin/articles")
    public Result<?> removeArticlePhysically(@NotEmpty @RequestBody List<Integer> articleIdList) {
        articleService.removeArticlesPhysically(articleIdList);
        return Result.ok();
    }

    @Operation(summary = "导出文章")
    @PostMapping("/admin/articles/export")
    public Result<List<String>> exportArticles(@NotEmpty @RequestBody List<Integer> articleIdList) {
        return Result.ok(articleService.exportArticles(articleIdList));
    }

    @OperatingLog(type = SAVE)
    @Operation(summary = "上传文章图片")
    @PostMapping("/admin/articles/images")
    public Result<String> uploadArticleImages(@NotNull @RequestParam("file")
                                              MultipartFile multipartFile) {
        return Result.ok(articleService.uploadArticleImages(multipartFile));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "导入文章")
    @PostMapping("/admin/articles/import")
    public Result<?> importArticle(@NotNull @RequestParam("file")
                                   MultipartFile multipartFile,
                                   @RequestParam(name = "type", required = false)
                                   String strategyName) {
        articleService.importArticle(multipartFile, strategyName);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "修改文章置顶状态")
    @PutMapping("/admin/articles/top")
    public Result<?> updateArticleTop(@Valid @RequestBody ArticleTopVO articleTopVO) {
        articleService.updateArticleTop(articleTopVO);
        return Result.ok();
    }

    @Operation(summary = "根据 ID 查看后台文章")
    @GetMapping("/admin/articles/{articleId}")
    public Result<ArticleVO> getAdminArticle
            (@NotNull @PathVariable("articleId") Integer articleId) {
        return Result.ok(articleService.getAdminArticle(articleId));
    }

    @Operation(summary = "查看首页文章")
    @GetMapping("/articles")
    public Result<List<HomePageArticleDTO>> viewHomePageArticles() {
        return Result.ok(articleService.listHomePageArticles());
    }

    @Operation(summary = "查看文章归档")
    @GetMapping("/articles/archives")
    public Result<PageDTO<ArticleArchiveDTO>> viewArticleArchives() {
        return Result.ok(articleService.listArticleArchives());
    }

    @Operation(summary = "前台根据 ID 查看文章")
    @GetMapping("/articles/{articleId}")
    public Result<ArticleDTO> getArticle(@NotNull @PathVariable("articleId")
                                         Integer articleId) {
        return Result.ok(articleService.getArticle(articleId));
    }

    @Operation(summary = "前台查看分类或标签文章预览")
    @GetMapping("/articles/condition")
    public Result<ArticlePreviewDTO> getArticlePreview(@Valid ArticlePreviewVO articlePreviewVO) {
        return Result.ok(articleService.getArticlePreview(articlePreviewVO));
    }

    @Operation(summary = "搜索文章")
    @GetMapping("/articles/search")
    public Result<List<ArticleSearchDTO>> searchArticle
            (@RequestParam(required = false) String keywords) {
        return Result.ok(articleService.listArticlesBySearch(keywords));
    }

    @Operation(summary = "点赞文章")
    @PostMapping("/articles/{articleId}/like")
    public Result<?> likeArticle(@NotNull @PathVariable Integer articleId) {
        articleService.likeArticle(articleId);
        return Result.ok();
    }

}
