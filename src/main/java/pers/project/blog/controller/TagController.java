package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.tag.ManageTagDTO;
import pers.project.blog.dto.tag.TagDTO;
import pers.project.blog.service.TagService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.tag.TagVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.REMOVE;
import static pers.project.blog.enums.OperationLogEnum.SAVE_OR_UPDATE;

/**
 * 标签控制器
 *
 * @author Luo Fei
 * @date 2023/1/8
 */
@Tag(name = "标签模块")
@Validated
@RestController
public class TagController {

    @Resource
    private TagService tagService;

    @Operation(summary = "浏览文章列表标签")
    @GetMapping("/admin/tags/search")
    public Result<List<TagDTO>> browseArticleTags
            (@RequestParam(required = false) String keywords) {
        return Result.ok(tagService.listArticleTags(keywords));
    }

    @Operation(summary = "查看后台标签管理")
    @GetMapping("/admin/tags")
    public Result<PageDTO<ManageTagDTO>> reviewTagManagement
            (@RequestParam(required = false) String keywords) {
        return Result.ok(tagService.listManageTags(keywords));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "添加或修改标签")
    @PostMapping("/admin/tags")
    public Result<?> saveOrUpdateTag(@Valid @RequestBody TagVO tagVO) {
        tagService.saveOrUpdateTag(tagVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除标签")
    @DeleteMapping("/admin/tags")
    public Result<?> removeTags(@NotEmpty @RequestBody List<Integer> tagIdList) {
        tagService.removeTags(tagIdList);
        return Result.ok();
    }

    @Operation(summary = "前台发现标签")
    @GetMapping("/tags")
    public Result<PageDTO<TagDTO>> listTags() {
        return Result.ok(tagService.listTags());
    }

}
