package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.comment.AdminCommentDTO;
import pers.project.blog.dto.comment.CommentDTO;
import pers.project.blog.dto.comment.ReplyDTO;
import pers.project.blog.service.CommentService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.comment.CommentSearchVO;
import pers.project.blog.vo.comment.CommentVO;
import pers.project.blog.vo.comment.ReviewVO;
import pers.project.blog.vo.comment.ViewCommentVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.REMOVE;
import static pers.project.blog.enums.OperationLogEnum.UPDATE;

/**
 * 评论控制器
 *
 * @author Luo Fei
 * @version 2023/1/10
 */
@Tag(name = "评论模块")
@Validated
@RestController
public class CommentController {

    @Resource
    private CommentService commentService;

    @Operation(summary = "发表评论")
    @PostMapping("/comments")
    public Result<?> comment(@Valid @RequestBody CommentVO commentVO) {
        commentService.saveComment(commentVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除评论")
    @DeleteMapping("/admin/comments")
    public Result<?> removeComments(@NotEmpty @RequestBody List<Integer> commentIdList) {
        commentService.removeComments(commentIdList);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "审核评论")
    @PutMapping("/admin/comments/review")
    public Result<?> reviewComment(@Valid @RequestBody ReviewVO reviewVO) {
        commentService.updateCommentsReview(reviewVO);
        return Result.ok();
    }

    @Operation(summary = "点赞评论")
    @PostMapping("/comments/{commentId}/like")
    public Result<?> likeComment(@NotNull @PathVariable Integer commentId) {
        commentService.likeComment(commentId);
        return Result.ok();
    }

    @Operation(summary = "查看后台评论管理")
    @GetMapping("/admin/comments")
    public Result<PageDTO<AdminCommentDTO>> reviewCommentManagement
            (@Valid CommentSearchVO commentSearchVO) {
        return Result.ok(commentService.listAdminComments(commentSearchVO));
    }

    @Operation(summary = "前台查看评论")
    @GetMapping("/comments")
    public Result<PageDTO<CommentDTO>> viewComments(@Valid ViewCommentVO viewCommentVO) {
        return Result.ok(commentService.listComments(viewCommentVO));
    }

    @Operation(summary = "查询评论下的回复")
    @GetMapping("/comments/{commentId}/replies")
    public Result<List<ReplyDTO>> viewRepliesUnderComment
            (@NotNull @PathVariable Integer commentId) {
        return Result.ok(commentService.listRepliesUnderComment(commentId));
    }

}
