package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.bloginfo.AdminBlogInfoDTO;
import pers.project.blog.dto.bloginfo.BlogInfoDTO;
import pers.project.blog.dto.bloginfo.WebsiteConfig;
import pers.project.blog.service.BlogInfoService;
import pers.project.blog.service.ChatRecordService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.bloginfo.InfoAboutMeVO;
import pers.project.blog.vo.bloginfo.VoiceVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static pers.project.blog.enums.OperationLogEnum.UPDATE;
import static pers.project.blog.enums.OperationLogEnum.UPLOAD;

/**
 * 博客信息控制器
 *
 * @author Luo Fei
 * @date 2022/12/28
 */
@Tag(name = "博客信息模块")
@Validated
@RestController
public class BlogInfoController {

    @Resource
    private BlogInfoService blogInfoService;
    @Resource
    private ChatRecordService chatRecordService;

    @Operation(summary = "统计访问量信息")
    @PostMapping("/report")
    public Result<?> updateVisitCount() {
        blogInfoService.updateVisitCount();
        return Result.ok();
    }

    @Operation(summary = "浏览博客信息")
    @GetMapping("/")
    public Result<BlogInfoDTO> browseBlogInfo() {
        return Result.ok(blogInfoService.getBlogInfo());
    }

    @Operation(summary = "浏览后台管理信息")
    @GetMapping("/admin")
    public Result<AdminBlogInfoDTO> browseAdminBlogInfo() {
        return Result.ok(blogInfoService.getBlogBackInfo());
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "更新网站配置")
    @PutMapping("/admin/website/config")
    public Result<?> updateWebSizeConfig(@Valid @RequestBody WebsiteConfig websiteConfig) {
        blogInfoService.updateWebSizeConfig(websiteConfig);
        return Result.ok();
    }

    @Operation(summary = "获取网站配置")
    @GetMapping("/admin/website/config")
    public Result<WebsiteConfig> getWebSiteConfig() {
        return Result.ok(blogInfoService.getWebSiteConfig());
    }

    @Operation(summary = "查看关于我信息")
    @GetMapping("/about")
    public Result<String> getInfoAboutMe() {
        return Result.ok(blogInfoService.getInfoAboutMe());
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "修改关于我信息")
    @PutMapping("/admin/about")
    public Result<?> updateInfoAboutMe(@Valid @RequestBody InfoAboutMeVO infoAboutMeVO) {
        blogInfoService.updateInfoAboutMe(infoAboutMeVO);
        return Result.ok();
    }

    @OperatingLog(type = UPLOAD)
    @Operation(summary = "上传博客配置图片")
    @PostMapping("/admin/config/images")
    public Result<String> uploadConfigImages(@NotNull @RequestParam("file")
                                             MultipartFile multipartFile) {
        return Result.ok(blogInfoService.uploadConfigImages(multipartFile));
    }

    @Operation(summary = "发送语音")
    @PostMapping("/voice")
    public Result<String> sendVoice(@Valid VoiceVO voiceVO) {
        return Result.ok(chatRecordService.sendVoice(voiceVO));
    }

}
