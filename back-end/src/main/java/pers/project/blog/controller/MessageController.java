package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.AdminMessageDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.service.MessageService;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.ReviewVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 留言控制器
 *
 * @author Luo Fei
 * @date 2023/1/10
 */
@Tag(name = "留言模块")
@RestController
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "查看后台留言列表")
    @GetMapping("/admin/messages")
    public Result<PageDTO<AdminMessageDTO>> listAdminMessages(ConditionVO conditionVO) {
        return Result.ok(messageService.listAdminMessages(conditionVO));
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "审核留言")
    @PutMapping("/admin/messages/review")
    public Result<?> updateMessagesReview(@Valid @RequestBody ReviewVO reviewVO) {
        messageService.updateMessagesReview(reviewVO);
        return Result.ok();
    }

    // TODO: 2023/1/10 留言和评论的删除都是物理删除
    @OperationLog(type = LogConstant.REMOVE)
    @Operation(summary = "删除留言")
    @DeleteMapping("/admin/messages")
    public Result<?> removeMessages(@RequestBody List<Integer> messageIdList) {
        messageService.removeBatchByIds(messageIdList);
        return Result.ok();
    }

}
