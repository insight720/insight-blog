package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.DirectoryUriConstant;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.AdminArticleDTO;
import pers.project.blog.dto.ArticleDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.service.ArticleService;
import pers.project.blog.strategy.context.ArticleImportContext;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.vo.ArticleTopVO;
import pers.project.blog.vo.ArticleVO;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.TableLogicVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 文章控制器
 *
 * @author Luo Fei
 * @date 2023/1/8
 */
@Tag(name = "文章模块")
@RestController
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @OperationLog(type = LogConstant.SAVE_OR_UPDATE)
    @Operation(summary = "导入文章")
    @PostMapping("/admin/articles/import")
    public Result<?> importArticle(@RequestParam("file") MultipartFile multipartFile,
                                   @RequestParam(name = "type", required = false)
                                   String strategyName) {
        ArticleImportContext.executeStrategy(multipartFile, strategyName);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.SAVE)
    @Operation(summary = "上传文章图片")
    @Parameter(name = "multipartFile", description = "文章",
            required = true, schema = @Schema(type = "MultipartFile"))
    @PostMapping("/admin/articles/images")
    public Result<String> saveArticleImage(@RequestParam("file") MultipartFile multipartFile) {
        return Result.ok(UploadContext.executeStrategy(multipartFile, DirectoryUriConstant.ARTICLE));
    }

    @OperationLog(type = LogConstant.SAVE_OR_UPDATE)
    @Operation(summary = "添加或修改文章")
    @PostMapping("/admin/articles")
    public Result<?> saveOrUpdateArticle(@Valid @RequestBody ArticleVO articleVO) {
        articleService.saveOrUpdateArticle(articleVO);
        return Result.ok();
    }

    @Operation(summary = "查看后台文章")
    @GetMapping("/admin/articles")
    public Result<PageDTO<AdminArticleDTO>> listAdminArticles(ConditionVO conditionVO) {
        return Result.ok(articleService.listAdminArticles(conditionVO));
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "恢复或逻辑删除文章")
    @PutMapping("/admin/articles")
    public Result<?> recoverOrRemoveArticleLogically(@Valid @RequestBody TableLogicVO tableLogicVO) {
        articleService.recoverOrRemoveArticleLogically(tableLogicVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.REMOVE)
    @Operation(summary = "物理删除文章")
    @DeleteMapping("/admin/articles")
    public Result<?> removeArticlePhysically(@RequestBody List<Integer> articleIdList) {
        articleService.removeArticlePhysically(articleIdList);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "修改文章置顶状态")
    @PutMapping("/admin/articles/top")
    public Result<?> updateArticleTop(@Valid @RequestBody ArticleTopVO articleTopVO) {
        articleService.updateArticleTop(articleTopVO);
        return Result.ok();
    }

    @Operation(summary = "根据 ID 查看后台文章")
    @Parameter(name = "articleId", description = "文章ID",
            required = true, schema = @Schema(type = "Integer"))
    @GetMapping("/admin/articles/{articleId}")
    public Result<ArticleDTO> getAdminArticleById
            (@PathVariable("articleId") Integer articleId) {
        return Result.ok(articleService.getAdminArticleById(articleId));
    }

}
