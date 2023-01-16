package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.AdminCommentDTO;
import pers.project.blog.dto.CommentDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.ReplyDTO;
import pers.project.blog.entity.CommentEntity;
import pers.project.blog.vo.CommentVO;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.ReviewVO;

import java.util.List;

/**
 * 针对表【tb_comment】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
public interface CommentService extends IService<CommentEntity> {

    /**
     * 查询后台的分页评论
     *
     * @param conditionVO 条件
     * @return {@code PageDTO<AdminCommentDTO>} 评论列表
     */
    PageDTO<AdminCommentDTO> listAdminComments(ConditionVO conditionVO);

    /**
     * 审核评论
     *
     * @param reviewVO 审核信息
     */
    void updateCommentsReview(ReviewVO reviewVO);

    /**
     * 查看分页的评论
     *
     * @param commentVO 评论信息
     * @return {@code PageDTO<CommentDTO>} 分页的评论数据列表
     */
    PageDTO<CommentDTO> listComments(CommentVO commentVO);

    /**
     * 添加评论
     *
     * @param commentVO 评论对象
     */
    void saveComment(CommentVO commentVO);

    /**
     * 点赞评论
     *
     * @param commentId 评论 ID
     */
    void saveCommentLike(Integer commentId);

    /**
     * 查看评论下的回复
     *
     * @param commentId 评论 ID
     * @return {@code List<ReplyDTO>} 回复列表
     */
    List<ReplyDTO> listRepliesByCommentId(Integer commentId);

}
