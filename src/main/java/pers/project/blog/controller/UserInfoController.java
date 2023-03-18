package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.userinfo.OnlineUserDTO;
import pers.project.blog.service.UserInfoService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.userinfo.EmailVO;
import pers.project.blog.vo.userinfo.UserDisableVO;
import pers.project.blog.vo.userinfo.UserInfoVO;
import pers.project.blog.vo.userinfo.UserRoleVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static pers.project.blog.enums.OperationLogEnum.UPDATE;
import static pers.project.blog.enums.OperationLogEnum.UPLOAD;


/**
 * 用户信息控制器
 *
 * @author Luo Fei
 * @version 2023/1/1
 */
@Tag(name = "用户信息模块")
@Validated
@RestController
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @OperatingLog(type = UPDATE)
    @Operation(summary = "修改用户禁用状态")
    @PutMapping("/admin/users/disable")
    public Result<?> updateUserDisable(@Valid @RequestBody UserDisableVO userDisableVO) {
        userInfoService.updateUserDisable(userDisableVO);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "修改用户昵称或角色")
    @PutMapping("/admin/users/role")
    public Result<?> updateNicknameOrRole(@Valid @RequestBody UserRoleVO userRoleVO) {
        userInfoService.updateNicknameOrRole(userRoleVO);
        return Result.ok();
    }

    @Operation(summary = "查看在线用户")
    @GetMapping("/admin/users/online")
    public Result<PageDTO<OnlineUserDTO>> viewOnlineUsers
            (@RequestParam(required = false) String keywords) {
        return Result.ok(userInfoService.listOnlineUsers(keywords));
    }

    @Operation(summary = "下线用户")
    @DeleteMapping("/admin/users/{userInfoId}/online")
    public Result<?> makeUserOffline(@NotNull @PathVariable Integer userInfoId) {
        userInfoService.makeUserOffline(userInfoId);
        return Result.ok();
    }

    @OperatingLog(type = UPLOAD)
    @Operation(summary = "上传用户头像")
    @PostMapping("/users/avatar")
    public Result<String> uploadUserAvatar
            (@NotNull @RequestParam("file") MultipartFile multipartFile) {
        return Result.ok(userInfoService.uploadUserAvatar(multipartFile));
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "修改用户信息")
    @PutMapping("/users/info")
    public Result<?> updateUserInfo(@Valid @RequestBody UserInfoVO userInfoVO) {
        userInfoService.updateUserInfo(userInfoVO);
        return Result.ok();
    }

    @Operation(summary = "修改用户邮箱")
    @PostMapping("/users/email")
    public Result<?> updateUserEmail(@Valid @RequestBody EmailVO emailVO) {
        userInfoService.saveUserEmail(emailVO);
        return Result.ok();
    }

}
