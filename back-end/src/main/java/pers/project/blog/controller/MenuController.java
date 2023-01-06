package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.LabelOptionDTO;
import pers.project.blog.dto.MenuDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.dto.UserMenuDTO;
import pers.project.blog.service.MenuService;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.MenuVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 后台管理系统菜单控制器
 *
 * @author Luo Fei
 * @date 2022/12/27
 */
@Tag(name = "菜单模块")
@RestController
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(summary = "查看当前用户菜单")
    @GetMapping("/admin/user/menus")
    public Result<List<UserMenuDTO>> listUserMenus() {
        return Result.ok(menuService.listUserMenus());
    }

    @Operation(summary = "查看角色菜单选项")
    @GetMapping("/admin/role/menus")
    public Result<List<LabelOptionDTO>> listMenuOptions() {
        return Result.ok(menuService.listMenuOptions());
    }

    @Operation(summary = "查看菜单列表")
    @GetMapping("/admin/menus")
    public Result<List<MenuDTO>> listMenus(ConditionVO conditionVO) {
        return Result.ok(menuService.listMenus(conditionVO));
    }

    @OperationLog(type = LogConstant.SAVE_OR_UPDATE)
    @Operation(summary = "新增或修改菜单")
    @PostMapping("/admin/menus")
    public Result<?> saveOrUpdateMenu(@Valid @RequestBody MenuVO menuVO) {
        menuService.saveOrUpdateMenu(menuVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.REMOVE)
    @Operation(summary = "删除菜单")
    @DeleteMapping("/admin/menus/{menuId}")
    public Result<?> removeMenu(@PathVariable("menuId") Integer menuId) {
        menuService.removeMenu(menuId);
        return Result.ok();
    }

}
