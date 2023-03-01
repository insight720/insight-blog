package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.AccessLimit;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.message.AdminMessageDTO;
import pers.project.blog.dto.message.MessageDTO;
import pers.project.blog.service.MessageService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.comment.ReviewVO;
import pers.project.blog.vo.message.MessageSearchVO;
import pers.project.blog.vo.message.MessageVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.REMOVE;
import static pers.project.blog.enums.OperationLogEnum.UPDATE;

/**
 * 留言控制器
 *
 * @author Luo Fei
 * @date 2023/1/10
 */
@Tag(name = "留言模块")
@Validated
@RestController
public class MessageController {

    @Resource
    private MessageService messageService;

    @AccessLimit(seconds = 30, maxCount = 1)
    @Operation(summary = "添加留言")
    @PostMapping("/messages")
    public Result<?> saveMessage(@Valid @RequestBody MessageVO messageVO) {
        messageService.saveMessage(messageVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除留言")
    @DeleteMapping("/admin/messages")
    public Result<?> removeMessages(@NotEmpty @RequestBody List<Integer> messageIdList) {
        messageService.removeMessages(messageIdList);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "审核留言")
    @PutMapping("/admin/messages/review")
    public Result<?> updateMessagesReview(@Valid @RequestBody ReviewVO reviewVO) {
        messageService.updateMessagesReview(reviewVO);
        return Result.ok();
    }

    @Operation(summary = "查看后台留言列表")
    @GetMapping("/admin/messages")
    public Result<PageDTO<AdminMessageDTO>> reviewMessageManagement
            (@Valid MessageSearchVO messageSearchVO) {
        return Result.ok(messageService.listAdminMessages(messageSearchVO));
    }

    @Operation(summary = "前台查看留言弹幕")
    @GetMapping("/messages")
    public Result<List<MessageDTO>> listMessages() {
        return Result.ok(messageService.listMessages());
    }

}
