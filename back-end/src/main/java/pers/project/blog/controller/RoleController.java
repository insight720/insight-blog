package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.dto.RoleDTO;
import pers.project.blog.dto.UserRoleDTO;
import pers.project.blog.service.RoleService;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.RoleVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色控制器
 *
 * @author Luo Fei
 * @date 2022/12/31
 */
@Tag(name = "角色模块")
@RestController
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "查询角色列表")
    @GetMapping("/admin/roles")
    public Result<PageDTO<RoleDTO>> listRoles(ConditionVO conditionVO) {
        return Result.ok(roleService.listRoles(conditionVO));
    }

    @Operation(summary = "查询用户角色选项")
    @GetMapping("/admin/users/role")
    public Result<List<UserRoleDTO>> listUserRoles() {
        return Result.ok(roleService.listUserRoles());
    }


    @OperationLog(type = LogConstant.SAVE_OR_UPDATE)
    @Operation(summary = "保存或更新角色")
    @PostMapping("/admin/role")
    public Result<?> saveOrUpdateRole(@Valid @RequestBody RoleVO roleVO) {
        roleService.saveOrUpdateRole(roleVO);
        return Result.ok();
    }

    // TODO: 2023/1/3 这是单一删除，批量删除和禁用功能不可用
    @OperationLog(type = LogConstant.REMOVE)
    @Operation(summary = "删除角色")
    @DeleteMapping("/admin/roles")
    public Result<?> removeRoles(@RequestBody List<Integer> roleIdList) {
        roleService.removeRoles(roleIdList);
        return Result.ok();
    }

}
