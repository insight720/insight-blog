package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.friendlink.AdminFriendLinkDTO;
import pers.project.blog.dto.friendlink.FriendLinkDTO;
import pers.project.blog.enums.OperationLogEnum;
import pers.project.blog.service.FriendLinkService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.friendlink.FriendLinkVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.SAVE_OR_UPDATE;

/**
 * 友链控制器
 *
 * @author Luo Fei
 * @date 2023/1/6
 */
@Tag(name = "友链模块")
@Validated
@RestController
public class FriendLinkController {

    @Resource
    private FriendLinkService friendLinkService;

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "保存或修改友链")
    @PostMapping("/admin/links")
    public Result<?> saveOrUpdateFriendLink(@Valid @RequestBody FriendLinkVO friendLinkVO) {
        friendLinkService.saveOrUpdateFriendLink(friendLinkVO);
        return Result.ok();
    }

    @OperatingLog(type = OperationLogEnum.REMOVE)
    @Operation(summary = "删除友链")
    @DeleteMapping("/admin/links")
    public Result<?> removeFriendLinks(@NotEmpty @RequestBody List<Integer> friendLinkIdList) {
        friendLinkService.removeFriendLinks(friendLinkIdList);
        return Result.ok();
    }

    @Operation(summary = "前台查看友链")
    @GetMapping("/links")
    public Result<List<FriendLinkDTO>> viewFriendLinks() {
        return Result.ok(friendLinkService.listFriendLinks());
    }

    @Operation(summary = "后台查看友链列表")
    @GetMapping("/admin/links")
    public Result<PageDTO<AdminFriendLinkDTO>> listAdminFriendLinks
            (@RequestParam(required = false) String keywords) {
        return Result.ok(friendLinkService.listAdminFriendLinks(keywords));
    }

}
