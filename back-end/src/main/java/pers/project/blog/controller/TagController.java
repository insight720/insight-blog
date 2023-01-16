package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.AdminTagDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.dto.TagDTO;
import pers.project.blog.service.TagService;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.TagVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 标签控制器
 *
 * @author Luo Fei
 * @date 2023/1/8
 */
@Tag(name = "标签模块")
@RestController
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Operation(summary = "查询后台标签列表")
    @GetMapping("/admin/tags")
    public Result<PageDTO<AdminTagDTO>> listAdminTags(ConditionVO conditionVO) {
        return Result.ok(tagService.listAdminTags(conditionVO));
    }

    @OperationLog(type = LogConstant.SAVE_OR_UPDATE)
    @Operation(summary = "添加或修改标签")
    @PostMapping("/admin/tags")
    public Result<?> saveOrUpdateTag(@Valid @RequestBody TagVO tagVO) {
        tagService.saveOrUpdateTag(tagVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.REMOVE)
    @Operation(summary = "删除标签")
    @DeleteMapping("/admin/tags")
    public Result<?> removeTags(@RequestBody List<Integer> tagIdList) {
        tagService.removeTags(tagIdList);
        return Result.ok();
    }

    @Operation(summary = "搜索文章标签")
    @GetMapping("/admin/tags/search")
    public Result<List<TagDTO>> listTagsBySearch(ConditionVO conditionVO) {
        return Result.ok(tagService.listTagsBySearch(conditionVO));
    }

    @Operation(summary = "查询标签列表")
    @GetMapping("/tags")
    public Result<PageDTO<TagDTO>> listTags() {
        return Result.ok(tagService.listTags());
    }

}
