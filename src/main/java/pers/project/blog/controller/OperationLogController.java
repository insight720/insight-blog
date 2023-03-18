package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.operationlog.OperationLogDTO;
import pers.project.blog.service.OperationLogService;
import pers.project.blog.util.Result;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 操作日志控制器
 *
 * @author Luo Fei
 * @version 2023/1/3
 */
@Tag(name = "日志模块")
@Validated
@RestController
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @Operation(summary = "查看操作日志")
    @GetMapping("/admin/operation/logs")
    public Result<PageDTO<OperationLogDTO>> viewOperationLogs
            (@RequestParam(required = false) String keywords) {
        return Result.ok(operationLogService.listOperationLogs(keywords));
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/admin/operation/logs")
    public Result<?> removeOperationLogs(@NotEmpty @RequestBody
                                         List<Integer> operationLogIdList) {
        operationLogService.removeOperationLogs(operationLogIdList);
        return Result.ok();
    }

}
