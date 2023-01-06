package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.AdminFriendLinkDTO;
import pers.project.blog.dto.FriendLinkDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.service.FriendLinkService;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.FriendLinkVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 友链控制器
 *
 * @author Luo Fei
 * @date 2023/1/6
 */
@Tag(name = "友链模块")
@RestController
public class FriendLinkController {

    private final FriendLinkService friendLinkService;

    public FriendLinkController(FriendLinkService friendLinkService) {
        this.friendLinkService = friendLinkService;
    }

    @Operation(summary = "查看友链列表")
    @GetMapping("/links")
    public Result<List<FriendLinkDTO>> listFriendLinks() {
        return Result.ok(friendLinkService.listFriendLinks());
    }

    @Operation(summary = "后台查看友链列表")
    @GetMapping("/admin/links")
    public Result<PageDTO<AdminFriendLinkDTO>> listAdminFriendLinks(ConditionVO conditionVO) {
        return Result.ok(friendLinkService.listAdminFriendLinks(conditionVO));
    }

    @OperationLog(type = LogConstant.SAVE_OR_UPDATE)
    @Operation(summary = "保存或修改友链")
    @PostMapping("/admin/links")
    public Result<?> saveOrUpdateFriendLink(@Valid @RequestBody FriendLinkVO friendLinkVO) {
        friendLinkService.saveOrUpdateFriendLink(friendLinkVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.REMOVE)
    @Operation(summary = "删除友链")
    @DeleteMapping("/admin/links")
    public Result<?> removeFriendLink(@RequestBody List<Integer> friendLinkIdList) {
        friendLinkService.removeBatchByIds(friendLinkIdList);
        return Result.ok();
    }

}
