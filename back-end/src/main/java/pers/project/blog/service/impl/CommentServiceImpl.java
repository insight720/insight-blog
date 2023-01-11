package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.blog.dto.AdminCommentDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.entity.CommentEntity;
import pers.project.blog.mapper.CommentMapper;
import pers.project.blog.service.CommentService;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.ReviewVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 针对表【tb_comment】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity> implements CommentService {

    @Override
    public PageDTO<AdminCommentDTO> listAdminComments(ConditionVO conditionVO) {
        // 计数符合条件的评论量
        Integer adminCommentsCount = baseMapper.countAdminComments(conditionVO);
        if (adminCommentsCount == 0) {
            return new PageDTO<>();
        }

        // 查询评论列表
        IPage<CommentEntity> page = PaginationUtils.getPage();
        List<AdminCommentDTO> adminCommentList
                = baseMapper.listAdminCategoryDTOs(page.offset(), page.getSize(), conditionVO);

        return PageDTO.of(adminCommentList, adminCommentsCount);
    }

    @Override
    public void updateCommentsReview(ReviewVO reviewVO) {
        Integer isReview = reviewVO.getIsReview();
        List<CommentEntity> commentEntityList = reviewVO.getIdList()
                .stream()
                .map(commentId -> CommentEntity.builder()
                        .id(commentId)
                        .isReview(isReview)
                        .build())
                .collect(Collectors.toList());
        updateBatchById(commentEntityList);
    }

}




