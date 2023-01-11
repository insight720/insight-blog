package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.AdminCommentDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.entity.CommentEntity;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.ReviewVO;

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

}
