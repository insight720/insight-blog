package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.resource.ManageResourceDTO;
import pers.project.blog.dto.resource.RoleResourceDTO;
import pers.project.blog.service.ResourceService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.resource.ResourceVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.REMOVE;
import static pers.project.blog.enums.OperationLogEnum.SAVE_OR_UPDATE;

/**
 * 资源控制器
 *
 * @author Luo Fei
 * @version 2023/1/2
 */
@Tag(name = "资源模块")
@Validated
@RestController
public class ResourceController {

    @Resource
    private ResourceService resourceService;

    @Operation(summary = "查看角色资源权限")
    @GetMapping("/admin/role/resources")
    public Result<List<RoleResourceDTO>> viewRoleResourceAuthority() {
        return Result.ok(resourceService.listRoleResources());
    }

    @Operation(summary = "查看资源管理")
    @GetMapping("/admin/resources")
    public Result<List<ManageResourceDTO>> reviewResourceManagement
            (@RequestParam(required = false) String keywords) {
        return Result.ok(resourceService.listManageResources(keywords));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "新增或修改资源")
    @PostMapping("/admin/resources")
    public Result<?> saveOrUpdateResource(@Valid @RequestBody ResourceVO resourceVO) {
        resourceService.saveOrUpdateResource(resourceVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除资源")
    @DeleteMapping("/admin/resources/{resourceId}")
    public Result<?> removeResource(@NotNull @PathVariable Integer resourceId) {
        resourceService.removeResource(resourceId);
        return Result.ok();
    }

}
