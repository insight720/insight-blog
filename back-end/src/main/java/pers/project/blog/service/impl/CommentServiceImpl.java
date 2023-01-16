package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import pers.project.blog.constant.BooleanConstant;
import pers.project.blog.constant.RedisConstant;
import pers.project.blog.constant.WebsiteConstant;
import pers.project.blog.constant.enumeration.CommentTypeEnum;
import pers.project.blog.dto.*;
import pers.project.blog.entity.CommentEntity;
import pers.project.blog.mapper.ArticleMapper;
import pers.project.blog.mapper.CommentMapper;
import pers.project.blog.mapper.TalkMapper;
import pers.project.blog.mapper.UserInfoMapper;
import pers.project.blog.service.BlogInfoService;
import pers.project.blog.service.CommentService;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.util.RabbitUtils;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.util.SecurityUtils;
import pers.project.blog.vo.CommentVO;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.ReviewVO;
import pers.project.blog.vo.WebsiteConfigVO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 针对表【tb_comment】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity> implements CommentService {

    private final BlogInfoService blogInfoService;

    private final TaskExecutor taskExecutor;

    private final ArticleMapper articleMapper;

    private final TalkMapper talkMapper;

    private final UserInfoMapper userInfoMapper;

    // TODO: 2023/1/16 优化
    @Value("${website.url}")
    private String websiteUrl;

    public CommentServiceImpl(BlogInfoService blogInfoService,
                              TaskExecutor taskExecutor, ArticleMapper articleMapper, TalkMapper talkMapper, UserInfoMapper userInfoMapper) {
        this.blogInfoService = blogInfoService;
        this.taskExecutor = taskExecutor;
        this.articleMapper = articleMapper;
        this.talkMapper = talkMapper;
        this.userInfoMapper = userInfoMapper;
    }

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

    @Override
    public PageDTO<CommentDTO> listComments(CommentVO commentVO) {
        // TODO: 2023/1/12 大接口
        // 查询符合条件的评论总量
        Long commentsCount = lambdaQuery()
                .eq(Objects.nonNull(commentVO.getTopicId()),
                        CommentEntity::getTopicId, commentVO.getTopicId())
                .eq(CommentEntity::getType, commentVO.getType())
                .isNull(CommentEntity::getParentId)
                .eq(CommentEntity::getIsReview, BooleanConstant.TRUE)
                .count();
        if (commentsCount == 0) {
            return new PageDTO<>();
        }

        // 分页查询符合条件的评论数据
        IPage<Object> page = PaginationUtils.getPage();
        List<CommentDTO> commentList = baseMapper.listCommentDTOs
                (page.offset(), page.getSize(), commentVO);
        if (CollectionUtils.isEmpty(commentList)) {
            return new PageDTO<>();
        }
        List<Integer> commentIdList = commentList
                .stream()
                .map(CommentDTO::getId)
                .collect(Collectors.toList());

        // 根据评论 ID 查询回复量
        Map<Integer, Integer> commentIdRepliesCountMap
                = baseMapper.listRepliesCounts(commentIdList);

        // 查询评论回复数据
        List<ReplyDTO> replyList = baseMapper.listReplyDTOs(commentIdList);

        // 查询评论点赞数据
        Map<String, Object> commentIdLikesCountMap
                = RedisUtils.hGetAll(RedisConstant.COMMENT_LIKE_COUNT);

        // 封装回复点赞量
        replyList.forEach(replyDTO -> replyDTO.setLikeCount
                ((Integer) commentIdLikesCountMap.get(replyDTO.getId())));

        // 根据评论 ID 分组回复数据
        Map<Integer, List<ReplyDTO>> commentIdRepliesMap = replyList
                .stream()
                .collect(Collectors.groupingBy(ReplyDTO::getParentId));

        // 封装评论数据
        commentList.forEach(commentDTO -> {
            Integer commentId = commentDTO.getId();
            commentDTO.setLikeCount((Integer) commentIdLikesCountMap.get(commentId.toString()));
            commentDTO.setReplyCount(commentIdRepliesCountMap.get(commentId));
            commentDTO.setReplyDTOList(commentIdRepliesMap.get(commentId));
        });

        return PageDTO.of(commentList, commentsCount.intValue());
    }

    @Override
    public void saveComment(CommentVO commentVO) {
        // 判断是否需要审核
        WebsiteConfigVO webSiteConfig = blogInfoService.getWebSiteConfig();
        Integer isReviewed = webSiteConfig.getIsCommentReview();

        // 保存数据
        CommentEntity commentEntity = CommentEntity.builder()
                .userId(SecurityUtils.getUserDetails().getUserInfoId())
                .replyUserId(commentVO.getReplyUserId())
                .topicId(commentVO.getTopicId())
                .commentContent(SecurityUtils.filter(commentVO.getCommentContent()))
                .parentId(commentVO.getParentId())
                .type(commentVO.getType())
                .isReview(isReviewed.equals(BooleanConstant.TRUE) ?
                        BooleanConstant.FALSE : BooleanConstant.TRUE)
                .build();
        save(commentEntity);

        // 判断是否开启邮箱通知，通知用户
        if (webSiteConfig.getIsEmailNotice().equals(BooleanConstant.TRUE)) {
            CompletableFuture.runAsync(() -> notice(commentEntity), taskExecutor);
        }
    }

    @Override
    public void saveCommentLike(Integer commentId) {
        // 判断是否点赞来决定是点赞还是取消赞
        String commentLikeKey = RedisConstant.COMMENT_USER_LIKE + SecurityUtils.getUserDetails().getUserInfoId();
        if (RedisUtils.sIsMember(commentLikeKey, commentId)) {
            RedisUtils.sRem(commentLikeKey, commentId);
            RedisUtils.hIncrBy(RedisConstant.COMMENT_LIKE_COUNT, commentId.toString(), -1L);
        } else {
            RedisUtils.sAdd(commentLikeKey, commentId);
            RedisUtils.hIncrBy(RedisConstant.COMMENT_LIKE_COUNT, commentId.toString(), 1L);
        }
    }

    @Override
    public List<ReplyDTO> listRepliesByCommentId(Integer commentId) {
        // 查询评论下的回复
        IPage<ReplyDTO> page = PaginationUtils.getPage();
        List<ReplyDTO> replyList = baseMapper.listRepliesByCommentId
                (page.offset(), page.getSize(), commentId);
        // TODO: 2023/1/16 每次都查全部 ？
        // 查询回复的点赞数
        Map<String, Object> replyIdLikeCountMap
                = RedisUtils.hGetAll(RedisConstant.COMMENT_LIKE_COUNT);

        replyList.forEach(reply -> reply.setLikeCount
                ((Integer) replyIdLikeCountMap.get(reply.getId().toString())));

        return replyList;
    }

    /**
     * 通知评论用户
     *
     * @param comment 评论信息
     */
    private void notice(CommentEntity comment) {
        // TODO: 2023/1/16 优化
        // 查询回复用户邮箱号
        Integer userId = WebsiteConstant.BLOGGER_ID;
        String id = Objects.nonNull(comment.getTopicId()) ?
                comment.getTopicId().toString() : "";
        if (comment.getReplyUserId() != null) {
            userId = comment.getReplyUserId();
        } else {
            switch (Objects.requireNonNull(CommentTypeEnum.getCommentEnum(comment.getType()))) {
                case ARTICLE:
                    userId = articleMapper.selectById(comment.getTopicId()).getUserId();
                    break;
                case TALK:
                    userId = talkMapper.selectById(comment.getTopicId()).getUserId();
                    break;
            }
        }

        String email = userInfoMapper.selectById(userId).getEmail();
        if (StringUtils.isNotBlank(email)) {
            // 发送消息
            EmailDTO emailDTO = new EmailDTO();
            // TODO: 2023/1/16 优化魔法值
            if (comment.getIsReview().equals(BooleanConstant.TRUE)) {
                // 评论提醒
                emailDTO.setEmail(email);
                emailDTO.setSubject("评论提醒");
                // 获取评论路径
                String url = websiteUrl + CommentTypeEnum.getCommentPath(comment.getType()) + id;
                emailDTO.setContent("您收到一条新的回复，请前往 " + url + "\n页面查看");
            } else {
                // 管理员审核提醒
                String adminEmail = userInfoMapper.selectById(WebsiteConstant.BLOGGER_ID).getEmail();
                emailDTO.setEmail(adminEmail);
                emailDTO.setSubject("审核提醒");
                emailDTO.setContent("您收到一条新的回复，请前往后台管理页面审核");
            }
            RabbitUtils.sendEmail(emailDTO);
        }
    }

}




