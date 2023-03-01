package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.talk.AdminTalkDTO;
import pers.project.blog.dto.talk.TalkDTO;
import pers.project.blog.service.TalkService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.talk.TalkVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.SAVE;
import static pers.project.blog.enums.OperationLogEnum.UPLOAD;

/**
 * 说说控制器
 *
 * @author Luo Fei
 * @date 2023/1/11
 */
@Tag(name = "说说模块")
@Validated
@RestController
public class TalkController {

    @Resource
    private TalkService talkService;

    @OperatingLog(type = UPLOAD)
    @Operation(summary = "上传说说图片")
    @PostMapping("/admin/talks/images")
    public Result<String> saveTalkImages(@NotNull @RequestParam("file")
                                         MultipartFile multipartFile) {
        return Result.ok(talkService.saveTalkImages(multipartFile));
    }

    @OperatingLog(type = SAVE)
    @Operation(summary = "保存或修改说说")
    @PostMapping("/admin/talks")
    public Result<?> saveOrUpdateTalk(@Valid @RequestBody TalkVO talkVO) {
        talkService.saveOrUpdateTalk(talkVO);
        return Result.ok();
    }

    @Operation(summary = "查看后台说说列表")
    @GetMapping("/admin/talks")
    public Result<PageDTO<AdminTalkDTO>> viewAdminTalks
            (@RequestParam(required = false) Integer status) {
        return Result.ok(talkService.listAdminTalks(status));
    }

    @Operation(summary = "获取待修改说说数据")
    @GetMapping("/admin/talks/{talkId}")
    public Result<AdminTalkDTO> getModifyingAdminTalk(@NotNull @PathVariable Integer talkId) {
        return Result.ok(talkService.getAdminTalk(talkId));
    }

    @Operation(summary = "删除说说")
    @DeleteMapping("/admin/talks")
    public Result<?> removeTalks(@NotEmpty @RequestBody List<Integer> talkIdList) {
        talkService.removeTalks(talkIdList);
        return Result.ok();
    }

    @Operation(summary = "浏览首页说说")
    @GetMapping("/home/talks")
    public Result<List<String>> browseHomePageTalks() {
        return Result.ok(talkService.listHomePageTalks());
    }

    @Operation(summary = "首页查看全部说说")
    @GetMapping("/talks")
    public Result<PageDTO<TalkDTO>> viewTalks() {
        return Result.ok(talkService.listTalks());
    }

    @Operation(summary = "点击查看一条说说")
    @GetMapping("/talks/{talkId}")
    public Result<TalkDTO> viewOneTalk(@NotNull @PathVariable Integer talkId) {
        return Result.ok(talkService.getTalk(talkId));
    }

    @Operation(summary = "点赞说说")
    @PostMapping("/talks/{talkId}/like")
    public Result<?> likeTalk(@NotNull @PathVariable Integer talkId) {
        talkService.likeTalk(talkId);
        return Result.ok();
    }

}
