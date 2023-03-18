package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.AccessLimit;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.userauth.AreaCountDTO;
import pers.project.blog.dto.userauth.UserDTO;
import pers.project.blog.dto.userauth.UserDetailsDTO;
import pers.project.blog.service.UserAuthService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.userauth.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.UPDATE;

/**
 * 用户账号控制器
 *
 * @author Luo Fei
 * @version 2022/12/29
 */
@Tag(name = "用户账号模块")
@Validated
@RestController
public class UserAuthController {

    @Resource
    private UserAuthService userAuthService;

    @Operation(summary = "浏览用户地域分布")
    @GetMapping("/admin/users/area")
    public Result<List<AreaCountDTO>> browseUserAreas
            (@NotNull @RequestParam("type") Integer userType) {
        return Result.ok(userAuthService.listUserAreas(userType));
    }

    @Operation(summary = "查看用户列表")
    @GetMapping("/admin/users")
    public Result<PageDTO<UserDTO>> viewUserList(UserSearchVO userSearchVO) {
        return Result.ok(userAuthService.listUsers(userSearchVO));
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "后台修改密码")
    @PutMapping("/admin/users/password")
    public Result<?> updateAdminPassword(@Valid @RequestBody PasswordVO passwordVO) {
        userAuthService.updateAdminPassword(passwordVO);
        return Result.ok();
    }

    @AccessLimit(seconds = 60, maxCount = 1)
    @Operation(summary = "发送验证码")
    @GetMapping("/users/code")
    public Result<?> sendVerificationCode(@RequestParam("username") String email) {
        userAuthService.sendVerificationCode(email);
        return Result.ok();
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody UserAuthVO userAuthVO) {
        userAuthService.register(userAuthVO);
        return Result.ok();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/users/password")
    public Result<?> resetPassword(@Valid @RequestBody UserAuthVO useuserAuthVO) {
        userAuthService.resetPassword(useuserAuthVO);
        return Result.ok();
    }

    @Operation(summary = "QQ 登录")
    @PostMapping("/users/oauth/qq")
    public Result<UserDetailsDTO> qqLogin(@Valid @RequestBody QQLoginVO qqLoginVO) {
        return Result.ok(userAuthService.qqLogin(qqLoginVO));
    }

    @Operation(summary = "微博登录")
    @PostMapping("/users/oauth/weibo")
    public Result<UserDetailsDTO> weiboLogin(@Valid @RequestBody WeiboLoginVO weiboLoginVO) {
        return Result.ok(userAuthService.weiboLogin(weiboLoginVO));
    }

}
