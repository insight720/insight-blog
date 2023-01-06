package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.Result;
import pers.project.blog.service.PageService;
import pers.project.blog.vo.PageVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 页面控制器
 *
 * @author Luo Fei
 * @date 2023/1/6
 */
@Tag(name = "页面模块")
@RestController
public class PageController {

    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @Operation(summary = "获取页面列表")
    @GetMapping("/admin/pages")
    public Result<List<PageVO>> listPages() {
        return Result.ok(pageService.listPages());
    }

    @OperationLog(type = LogConstant.SAVE_OR_UPDATE)
    @Operation(summary = "保存或更新页面")
    @PostMapping("/admin/pages")
    public Result<?> saveOrUpdatePage(@Valid @RequestBody PageVO pageVO) {
        pageService.saveOrUpdatePage(pageVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.REMOVE)
    @Operation(summary = "删除页面")
    @Parameter(name = "pageId", description = "页面 ID", required = true,
            schema = @Schema(type = "Integer"))
    @DeleteMapping("/admin/pages/{pageId}")
    public Result<?> removePage(@PathVariable("pageId") Integer pageId) {
        pageService.removePage(pageId);
        return Result.ok();
    }

}
