package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.blog.dto.AdminUserDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.dto.UserAreaDTO;
import pers.project.blog.service.UserAuthService;
import pers.project.blog.vo.ConditionVO;

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

    @Operation(summary = "获取用户属地分布")
    @GetMapping("/admin/users/area")
    public Result<List<UserAreaDTO>> listUserAreas(ConditionVO conditionVO) {
        return Result.ok(userAuthService.listUserAreas(conditionVO));
    }

    @Operation(summary = "查询后台用户列表")
    @GetMapping("/admin/users")
    public Result<PageDTO<AdminUserDTO>> listBackgroundUsers(ConditionVO conditionVO) {
        return Result.ok(userAuthService.listBackgroundUserDTOs(conditionVO));
    }

}
