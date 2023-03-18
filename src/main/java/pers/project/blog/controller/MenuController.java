package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.menu.ManageMenuDTO;
import pers.project.blog.dto.menu.RoleMenuDTO;
import pers.project.blog.dto.menu.UserMenuDTO;
import pers.project.blog.service.MenuService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.menu.HiddenMenuVO;
import pers.project.blog.vo.menu.MenuVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.*;

/**
 * 菜单控制器
 *
 * @author Luo Fei
 * @version 2022/12/27
 */
@Tag(name = "菜单模块")
@Validated
@RestController
public class MenuController {

    @Resource
    private MenuService menuService;

    @Operation(summary = "浏览用户菜单")
    @GetMapping("/admin/user/menus")
    public Result<List<UserMenuDTO>> browseUserMenu() {
        return Result.ok(menuService.listUserMenus());
    }

    @Operation(summary = "查看角色菜单权限")
    @GetMapping("/admin/role/menus")
    public Result<List<RoleMenuDTO>> viewRoleMenuAuthority() {
        return Result.ok(menuService.listRoleMenus());
    }

    @Operation(summary = "查看菜单管理")
    @GetMapping("/admin/menus")
    public Result<List<ManageMenuDTO>> reviewMenuManagement
            (@RequestParam(required = false) String keywords) {
        return Result.ok(menuService.listManageMenus(keywords));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "新增或修改菜单")
    @PostMapping("/admin/menus")
    public Result<?> saveOrUpdateMenu(@Valid @RequestBody MenuVO menuVO) {
        menuService.saveOrUpdateMenu(menuVO);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "修改菜单隐藏状态")
    @PutMapping("/admin/users/hidden")
    public Result<?> updateHiddenStatus(@Valid @RequestBody HiddenMenuVO hiddenMenuVO) {
        menuService.updateHiddenStatus(hiddenMenuVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除菜单")
    @DeleteMapping("/admin/menus/{menuId}")
    public Result<?> removeMenu(@NotNull @PathVariable Integer menuId) {
        menuService.removeMenu(menuId);
        return Result.ok();
    }

}
