package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.comment.AdminCommentDTO;
import pers.project.blog.dto.comment.CommentDTO;
import pers.project.blog.dto.comment.ReplyDTO;
import pers.project.blog.entity.Comment;
import pers.project.blog.vo.comment.CommentSearchVO;
import pers.project.blog.vo.comment.CommentVO;
import pers.project.blog.vo.comment.ReviewVO;
import pers.project.blog.vo.comment.ViewCommentVO;

import java.util.List;

/**
 * 针对表【tb_comment】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
public interface CommentService extends IService<Comment> {

    /**
     * 保存评论
     */
    void saveComment(CommentVO commentVO);

    /**
     * 删除评论
     */
    void removeComments(List<Integer> commentIdList);

    /**
     * 审核评论
     */
    void updateCommentsReview(ReviewVO reviewVO);

    /**
     * 点赞评论
     */
    void likeComment(Integer commentId);

    /**
     * 获取分页的后台评论管理数据
     */
    PageDTO<AdminCommentDTO> listAdminComments(CommentSearchVO commentSearchVO);

    /**
     * 查询分页的前台查看评论所需数据
     */
    PageDTO<CommentDTO> listComments(ViewCommentVO viewCommentVO);

    /**
     * 查看评论下的回复
     */
    List<ReplyDTO> listRepliesUnderComment(Integer commentId);

}
