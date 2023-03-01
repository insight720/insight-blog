package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.comment.AdminCommentDTO;
import pers.project.blog.dto.comment.CommentDTO;
import pers.project.blog.dto.comment.ReplyCountDTO;
import pers.project.blog.dto.comment.ReplyDTO;
import pers.project.blog.dto.talk.CommentCountDTO;
import pers.project.blog.entity.Comment;
import pers.project.blog.vo.comment.CommentSearchVO;
import pers.project.blog.vo.comment.ViewCommentVO;

import java.util.List;

/**
 * 针对表【tb_comment】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 统计后台评论管理的评论数量
     */
    Long countAdminComments(@Param("commentSearchVO") CommentSearchVO commentSearchVO);

    /**
     * 查询分页的后台评论管理数据
     */
    List<AdminCommentDTO> listAdminComments(@Param("offset") long offset,
                                            @Param("size") long size,
                                            @Param("commentSearchVO")
                                            CommentSearchVO commentSearchVO);

    /**
     * 分页查询前台查看评论所需数据
     */
    List<CommentDTO> listComments(@Param("offset") long offset,
                                  @Param("size") long size,
                                  @Param("viewCommentVO") ViewCommentVO viewCommentVO);

    /**
     * 根据评论 ID 查询回复总量
     */
    List<ReplyCountDTO> listRepliesCounts(@Param("commentIdList") List<Integer> commentIdList);

    /**
     * 根据评论 ID 列表列出回复数据
     */
    List<ReplyDTO> listReplies(@Param("commentIdList") List<Integer> commentIdList);

    /**
     * 获取评论主题 ID 与评论数量数据
     */
    List<CommentCountDTO> getTopicIdAndCommentsCount(@Param("talkIdList") List<Integer> talkIdList);

    /**
     * 查看某条评论下的回复数据
     */
    List<ReplyDTO> listRepliesUnderComment(@Param("offset") long offset,
                                           @Param("size") long size,
                                           @Param("commentId") Integer commentId);

}




