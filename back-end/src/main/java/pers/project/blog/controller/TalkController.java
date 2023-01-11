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
import pers.project.blog.dto.AdminTalkDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.dto.TalkDTO;
import pers.project.blog.service.TalkService;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.TalkVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 说说控制器
 *
 * @author Luo Fei
 * @date 2023/1/11
 */
@Tag(name = "说说模块")
@RestController
public class TalkController {

    private final TalkService talkService;

    public TalkController(TalkService talkService) {
        this.talkService = talkService;
    }

    @Operation(summary = "查看后台说说列表")
    @GetMapping("/admin/talks")
    public Result<PageDTO<AdminTalkDTO>> listAdminTalks(ConditionVO conditionVO) {
        return Result.ok(talkService.listAdminTalks(conditionVO));
    }

    @OperationLog(type = LogConstant.SAVE_OR_UPDATE)
    @Operation(summary = "保存或修改说说")
    @PostMapping("/admin/talks")
    public Result<?> saveOrUpdateTalk(@Valid @RequestBody TalkVO talkVO) {
        talkService.saveOrUpdateTalk(talkVO);
        return Result.ok();
    }

    @Operation(summary = "删除说说")
    @DeleteMapping("/admin/talks")
    public Result<?> removeTalks(@RequestBody List<Integer> talkIdList) {
        talkService.removeBatchByIds(talkIdList);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.UPLOAD)
    @Operation(summary = "上传说说图片")
    @Parameter(name = "file", description = "说说图片",
            required = true, schema = @Schema(type = "MultipartFile"))
    @PostMapping("/admin/talks/images")
    public Result<String> saveTalkImages(@RequestParam("file") MultipartFile multipartFile) {
        return Result.ok(UploadContext.executeStrategy
                (multipartFile, DirectoryUriConstant.TALK));
    }

    @Operation(summary = "根据 ID 查看后台说说")
    @Parameter(name = "talkId", description = "说说 ID",
            required = true, schema = @Schema(type = "Integer"))
    @GetMapping("/admin/talks/{talkId}")
    public Result<AdminTalkDTO> getAdminTalk(@PathVariable("talkId") Integer talkId) {
        return Result.ok(talkService.getAdminTalk(talkId));
    }

    @Operation(summary = "查看首页说说")
    @GetMapping("/home/talks")
    public Result<List<String>> listHomePageTalks() {
        return Result.ok(talkService.listHomePageTalks());
    }

    @Operation(summary = "查看说说列表")
    @GetMapping("/talks")
    public Result<PageDTO<TalkDTO>> listTalks() {
        return Result.ok(talkService.listTalks());
    }

    @Operation(summary = "根据 ID 查看说说")
    @Parameter(name = "talkId", description = "说说 ID",
            required = true, schema = @Schema(type = "Integer"))
    @GetMapping("/talks/{talkId}")
    public Result<TalkDTO> getTalk(@PathVariable("talkId") Integer talkId) {
        return Result.ok(talkService.getTalk(talkId));
    }

    @Operation(summary = "点赞说说")
    @Parameter(name = "talkId", description = "说说 ID",
            required = true, schema = @Schema(type = "Integer"))
    @PostMapping("/talks/{talkId}/like")
    public Result<?> saveTalkLike(@PathVariable("talkId") Integer talkId) {
        talkService.saveTalkLike(talkId);
        return Result.ok();
    }

}
