package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.AccessLimit;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.*;
import pers.project.blog.service.UserAuthService;
import pers.project.blog.vo.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户账号控制器
 *
 * @author Luo Fei
 * @date 2022/12/29
 */
@Tag(name = "用户账号模块")
@RestController
public class UserAuthController {

    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @Operation(summary = "查询后台用户列表")
    @GetMapping("/admin/users")
    public Result<PageDTO<AdminUserDTO>> listBackgroundUsers(ConditionVO conditionVO) {
        return Result.ok(userAuthService.listBackgroundUserDTOs(conditionVO));
    }

    @Operation(summary = "获取用户属地分布")
    @GetMapping("/admin/users/area")
    public Result<List<UserAreaDTO>> listUserAreas(ConditionVO conditionVO) {
        return Result.ok(userAuthService.listUserAreas(conditionVO));
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "修改管理员密码")
    @PutMapping("/admin/users/password")
    public Result<?> updateAdminPassword(@Valid @RequestBody PasswordVO passwordVO) {
        userAuthService.updateAdminPassword(passwordVO);
        return Result.ok();
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody UserVO userVO) {
        userAuthService.register(userVO);
        return Result.ok();
    }

    @AccessLimit(seconds = 60, maxCount = 1)
    @Operation(summary = "发送邮箱验证码")
    @Parameter(name = "username", description = "用户名",
            required = true, schema = @Schema(type = "String"))
    @GetMapping("/users/code")
    public Result<?> sendCode(String username) {
        userAuthService.sendCode(username);
        return Result.ok();
    }

    @Operation(summary = "QQ 登录")
    @PostMapping("/users/oauth/qq")
    public Result<UserInfoDTO> qqLogin(@Valid @RequestBody QQLoginVO qqLoginVO) {
        return Result.ok(userAuthService.qqLogin(qqLoginVO));
    }

    @Operation(summary = "微博登录")
    @PostMapping("/users/oauth/weibo")
    public Result<UserInfoDTO> weiboLogin(@Valid @RequestBody WeiboLoginVO weiboLoginVO) {
        return Result.ok(userAuthService.weiboLogin(weiboLoginVO));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/users/password")
    public Result<?> updatePassword(@Valid @RequestBody UserVO userVO) {
        userAuthService.updatePassword(userVO);
        return Result.ok();
    }

}
