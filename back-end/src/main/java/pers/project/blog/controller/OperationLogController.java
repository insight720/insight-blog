package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.project.blog.dto.OperationLogDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.service.OperationLogService;
import pers.project.blog.vo.ConditionVO;

import java.util.List;

/**
 * 操作日志控制器
 *
 * @author Luo Fei
 * @date 2023/1/3
 */
@Tag(name = "日志模块")
@RestController
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Operation(summary = "查看操作日志")
    @GetMapping("/admin/operation/logs")
    public Result<PageDTO<OperationLogDTO>> listOperationLogs(ConditionVO conditionVO) {
        return Result.ok(operationLogService.listOperationLogs(conditionVO));
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/admin/operation/logs")
    public Result<?> removeOperationLogs(@RequestBody List<Integer> operationLogIdList) {
        operationLogService.removeBatchByIds(operationLogIdList);
        return Result.ok();
    }

}
