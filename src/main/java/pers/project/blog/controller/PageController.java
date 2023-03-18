package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.service.PageService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.page.PageVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.REMOVE;
import static pers.project.blog.enums.OperationLogEnum.SAVE_OR_UPDATE;

/**
 * 页面控制器
 *
 * @author Luo Fei
 * @version 2023/1/6
 */
@Tag(name = "页面模块")
@Validated
@RestController
public class PageController {

    @Resource
    private PageService pageService;

    @Operation(summary = "后台查看页面管理")
    @GetMapping("/admin/pages")
    public Result<List<PageVO>> reviewPageManagement() {
        return Result.ok(pageService.listPages());
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "保存或更新页面")
    @PostMapping("/admin/pages")
    public Result<?> saveOrUpdatePage(@Valid @RequestBody PageVO pageVO) {
        pageService.saveOrUpdatePage(pageVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除页面")
    @DeleteMapping("/admin/pages/{pageId}")
    public Result<?> removePage(@NotNull @PathVariable Integer pageId) {
        pageService.removePage(pageId);
        return Result.ok();
    }

}
