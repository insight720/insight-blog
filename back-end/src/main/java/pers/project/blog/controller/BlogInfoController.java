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
import pers.project.blog.dto.AdminBlogInfoDTO;
import pers.project.blog.dto.BlogInfoDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.service.BlogInfoService;
import pers.project.blog.service.ChatRecordService;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.vo.InfoAboutMeVO;
import pers.project.blog.vo.VoiceVO;
import pers.project.blog.vo.WebsiteConfigVO;

import javax.validation.Valid;
import java.io.IOException;

/**
 * 博客信息控制器
 *
 * @author Luo Fei
 * @date 2022/12/28
 */
@Tag(name = "博客信息模块")
@RestController
public class BlogInfoController {

    private final BlogInfoService blogInfoService;

    private final ChatRecordService chatRecordService;

    public BlogInfoController(BlogInfoService blogInfoService,
                              ChatRecordService chatRecordService) {
        this.blogInfoService = blogInfoService;
        this.chatRecordService = chatRecordService;
    }

    @Operation(summary = "查看博客信息")
    @GetMapping("/")
    public Result<BlogInfoDTO> getBlogInfoDTO() {
        return Result.ok(blogInfoService.getBlogInfo());
    }

    @Operation(summary = "查看后台信息")
    @GetMapping("/admin")
    public Result<AdminBlogInfoDTO> getBlogBackInfo() {
        return Result.ok(blogInfoService.getBlogBackInfo());
    }

    @Operation(summary = "统计访客信息")
    @PostMapping("/report")
    public Result<?> report() {
        blogInfoService.report();
        return Result.ok();
    }

    @Operation(summary = "获取网站配置")
    @GetMapping("/admin/website/config")
    public Result<WebsiteConfigVO> getWebSiteConfig() {
        return Result.ok(blogInfoService.getWebSiteConfig());
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "更新网站配置")
    @PutMapping("/admin/website/config")
    public Result<?> updateWebSizeConfig(@Valid @RequestBody WebsiteConfigVO websiteConfigVO) {
        blogInfoService.updateWebSizeConfig(websiteConfigVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.UPLOAD)
    @Operation(summary = "上传博客配置图片")
    @Parameter(name = "multipartFile", description = "配置图片",
            required = true, schema = @Schema(type = "MultipartFile"))
    @PostMapping("/admin/config/images")
    public Result<String> uploadConfigImage(@RequestParam("file") MultipartFile multipartFile) {
        return Result.ok(UploadContext.executeStrategy(multipartFile, DirectoryUriConstant.CONFIG));
    }

    @Operation(summary = "查看关于我信息")
    @GetMapping("/about")
    public Result<String> getInfoAboutMe() {
        return Result.ok(blogInfoService.getInfoAboutMe());
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "修改关于我信息")
    @PutMapping("/admin/about")
    public Result<?> updateInfoAboutMe(@Valid @RequestBody InfoAboutMeVO infoAboutMeVO) {
        blogInfoService.updateInfoAboutMe(infoAboutMeVO);
        return Result.ok();
    }

    @Operation(summary = "发送语音")
    @PostMapping("/voice")
    public Result<String> sendVoice(VoiceVO voiceVO) throws IOException {
        // TODO: 2023/1/13 可以不抛异常
        chatRecordService.sendVoice(voiceVO);
        return null;
    }

}
