package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.AdminCommentDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.service.CommentService;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.ReviewVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 评论控制器
 *
 * @author Luo Fei
 * @date 2023/1/10
 */
@Tag(name = "评论模块")
@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "查询后台评论")
    @GetMapping("/admin/comments")
    public Result<PageDTO<AdminCommentDTO>> listAdminComments(ConditionVO conditionVO) {
        return Result.ok(commentService.listAdminComments(conditionVO));
    }

    @OperationLog(type = LogConstant.REMOVE)
    @Operation(summary = "删除评论")
    @DeleteMapping("/admin/comments")
    public Result<?> removeComments(@RequestBody List<Integer> commentIdList) {
        commentService.removeBatchByIds(commentIdList);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "审核评论")
    @PutMapping("/admin/comments/review")
    public Result<?> updateCommentsReview(@Valid @RequestBody ReviewVO reviewVO) {
        commentService.updateCommentsReview(reviewVO);
        return Result.ok();
    }

}
