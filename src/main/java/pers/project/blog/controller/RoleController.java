package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.role.ManageRoleDTO;
import pers.project.blog.dto.role.UserRoleDTO;
import pers.project.blog.service.RoleService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.role.DisableRoleVO;
import pers.project.blog.vo.role.RoleVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.*;

/**
 * 角色控制器
 *
 * @author Luo Fei
 * @date 2022/12/31
 */
@Tag(name = "角色模块")
@Validated
@RestController
public class RoleController {

    @Resource
    private RoleService roleService;

    @Operation(summary = "查看用户列表角色")
    @GetMapping("/admin/users/role")
    public Result<List<UserRoleDTO>> viewUserRole() {
        return Result.ok(roleService.listUserRoles());
    }

    @Operation(summary = "查看角色管理")
    @GetMapping("/admin/roles")
    public Result<PageDTO<ManageRoleDTO>> reviewRoleManagement
            (@RequestParam(required = false) String keywords) {
        return Result.ok(roleService.listManageRoles(keywords));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "保存或更新角色")
    @PostMapping("/admin/role")
    public Result<?> saveOrUpdateRole(@Valid @RequestBody RoleVO roleVO) {
        roleService.saveOrUpdateRole(roleVO);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "修改角色禁用状态")
    @PutMapping("/admin/roles/disable")
    public Result<?> updateDisableStatus(@Valid @RequestBody DisableRoleVO disableRoleVO) {
        roleService.updateDisableStatus(disableRoleVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除角色")
    @DeleteMapping("/admin/roles")
    public Result<?> removeRoles(@NotEmpty @RequestBody List<Integer> roleIdList) {
        roleService.removeRoles(roleIdList);
        return Result.ok();
    }

}
