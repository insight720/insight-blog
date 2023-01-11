package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.dto.UserOnlineDTO;
import pers.project.blog.service.UserInfoService;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.UserDisableVO;
import pers.project.blog.vo.UserInfoVO;
import pers.project.blog.vo.UserRoleVO;

import javax.validation.Valid;


/**
 * 用户信息控制器
 *
 * @author Luo Fei
 * @date 2023/1/1
 */
@Tag(name = "用户信息模块")
@RestController
public class UserInfoController {

    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "修改用户禁用状态")
    @PutMapping("/admin/users/disable")
    public Result<?> updateUserDisable(@Valid @RequestBody UserDisableVO userDisableVO) {
        userInfoService.updateUserDisable(userDisableVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "修改用户角色")
    @PutMapping("/admin/users/role")
    public Result<?> updateUserRole(@Valid @RequestBody UserRoleVO userRoleVO) {
        userInfoService.updateUserRole(userRoleVO);
        return Result.ok();
    }

    @Operation(summary = "查看在线用户")
    @GetMapping("/admin/users/online")
    public Result<PageDTO<UserOnlineDTO>> listOnlineUsers(ConditionVO conditionVO) {
        return Result.ok(userInfoService.listOnlineUsers(conditionVO));
    }

    @Operation(summary = "下线用户")
    @DeleteMapping("/admin/users/{userInfoId}/online")
    public Result<?> removeOnlineUser(@PathVariable("userInfoId") Integer userInfoId) {
        userInfoService.removeOnlineUser(userInfoId);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.UPLOAD)
    @Operation(summary = "更新用户头像")
    @Parameter(name = "multipartFile", description = "用户头像", schema = @Schema(type = "MultipartFile"))
    @PostMapping("/users/avatar")
    public Result<String> updateUserAvatar(@RequestParam("file") MultipartFile multipartFile) {
        return Result.ok(userInfoService.updateUserAvatar(multipartFile));
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "更新用户信息")
    @PutMapping("/users/info")
    public Result<?> updateUserInfo(@Valid @RequestBody UserInfoVO userInfoVO) {
        userInfoService.updateUserInfo(userInfoVO);
        return Result.ok();
    }

}
