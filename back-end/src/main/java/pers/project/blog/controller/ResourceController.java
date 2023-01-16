package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.LabelOptionDTO;
import pers.project.blog.dto.ResourceDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.service.ResourceService;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.ResourceVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 资源控制器
 *
 * @author Luo Fei
 * @date 2023/1/2
 */
@Tag(name = "资源模块")
@RestController
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Operation(summary = "查看资源列表")
    @GetMapping("/admin/resources")
    public Result<List<ResourceDTO>> listResources(ConditionVO conditionVO) {
        return Result.ok(resourceService.listResources(conditionVO));
    }

    @OperationLog(type = LogConstant.SAVE_OR_UPDATE)
    @Operation(summary = "新增或修改资源")
    @PostMapping("/admin/resources")
    public Result<?> saveOrUpdateResource(@Valid @RequestBody ResourceVO resourceVO) {
        resourceService.saveOrUpdateResource(resourceVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.REMOVE)
    @Operation(summary = "删除资源")
    @DeleteMapping("/admin/resources/{resourceId}")
    public Result<?> removeResource(@PathVariable("resourceId") Integer resourceId) {
        resourceService.removeResource(resourceId);
        return Result.ok();
    }

    @Operation(summary = "查看资源角色选项")
    @GetMapping("/admin/role/resources")
    public Result<List<LabelOptionDTO>> listResourceOptions() {
        return Result.ok(resourceService.listResourceOptions());
    }

}
