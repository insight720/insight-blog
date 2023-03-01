package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.bloginfo.WebsiteConfig;
import pers.project.blog.dto.comment.*;
import pers.project.blog.entity.Comment;
import pers.project.blog.enums.CommentTypeEnum;
import pers.project.blog.mapper.ArticleMapper;
import pers.project.blog.mapper.CommentMapper;
import pers.project.blog.mapper.TalkMapper;
import pers.project.blog.mapper.UserInfoMapper;
import pers.project.blog.service.CommentService;
import pers.project.blog.util.*;
import pers.project.blog.vo.comment.CommentSearchVO;
import pers.project.blog.vo.comment.CommentVO;
import pers.project.blog.vo.comment.ReviewVO;
import pers.project.blog.vo.comment.ViewCommentVO;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static pers.project.blog.constant.GenericConst.*;
import static pers.project.blog.constant.RedisConst.COMMENT_LIKE_COUNT;
import static pers.project.blog.constant.RedisConst.COMMENT_LIKE_PREFIX;
import static pers.project.blog.enums.CommentTypeEnum.ARTICLE;
import static pers.project.blog.enums.CommentTypeEnum.TALK;

/**
 * 针对表【tb_comment】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private TalkMapper talkMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void saveComment(CommentVO commentVO) {
        // 判断是否开启评论审核
        Integer isCommentReview = ConfigUtils.getCache
                (WebsiteConfig::getIsCommentReview);
        // 组装评论数据
        Comment comment = Comment.builder()
                .userId(SecurityUtils.getUserInfoId())
                .replyUserId(commentVO.getReplyUserId())
                .topicId(commentVO.getTopicId())
                // 过滤评论内容
                .commentContent(StrRegexUtils.filter(commentVO.getCommentContent()))
                .parentId(commentVO.getParentId())
                .type(commentVO.getType())
                // 不开启评论审核则评论默认状态为已审核
                .isReview(isCommentReview.equals(TRUE_OF_INT) ? FALSE_OF_INT : TRUE_OF_INT)
                .build();
        // 保存评论
        save(comment);
        // 判断是否开启邮箱通知评论
        if (ConfigUtils.getCache
                (WebsiteConfig::getIsEmailNotice).equals(TRUE_OF_INT)) {
            // 通知用户有新评论或通知博主审核评论
            AsyncUtils.runAsync(() -> notify(comment));
        }
    }

    @Override
    public void removeComments(List<Integer> commentIdList) {
        removeBatchByIds(commentIdList);
    }

    @Override
    public void updateCommentsReview(ReviewVO reviewVO) {
        Integer isReview = reviewVO.getIsReview();
        List<Comment> commentList = reviewVO.getIdList()
                .stream()
                .map(commentId -> Comment.builder()
                        .id(commentId)
                        .isReview(isReview)
                        .build())
                .collect(Collectors.toList());
        updateBatchById(commentList);
    }

    @Override
    public void likeComment(Integer commentId) {
        // 判断是否点过赞来确定是点赞还是取消赞
        RedisUtils.likeOrUnlike(COMMENT_LIKE_PREFIX, COMMENT_LIKE_COUNT, commentId);
    }

    @Override
    public PageDTO<AdminCommentDTO> listAdminComments(CommentSearchVO commentSearchVO) {
        // 查询符合条件的评论量
        Long adminCommentsCount = baseMapper.countAdminComments(commentSearchVO);
        if (adminCommentsCount.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        // 查询分页的评论数据列表
        List<AdminCommentDTO> adminCommentList = baseMapper.listAdminComments
                (PageUtils.offset(), PageUtils.size(), commentSearchVO);
        return PageUtils.build(adminCommentList, adminCommentsCount);
    }

    @Override
    public PageDTO<CommentDTO> listComments(ViewCommentVO viewCommentVO) {
        // 查询符合条件的评论总量
        Long commentsCount = lambdaQuery()
                .eq(Objects.nonNull(viewCommentVO.getTopicId()),
                        Comment::getTopicId, viewCommentVO.getTopicId())
                .eq(Comment::getType, viewCommentVO.getType())
                .eq(Comment::getIsReview, TRUE_OF_INT)
                .isNull(Comment::getParentId)
                .count();
        if (commentsCount.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        // 分页查询符合条件的评论数据
        List<CommentDTO> commentList = baseMapper.listComments
                (PageUtils.offset(), PageUtils.size(), viewCommentVO);
        // 查询评论点赞数据
        CompletableFuture<Map<String, Object>> likeFuture
                = AsyncUtils.supplyAsync(() -> RedisUtils.hGetAll(COMMENT_LIKE_COUNT));
        // 获取评论 ID 列表
        List<Integer> commentIdList = commentList
                .stream()
                .map(CommentDTO::getId)
                .collect(Collectors.toList());
        // 根据评论 ID 查询回复量
        CompletableFuture<Map<Integer, Integer>> replayCountFuture = AsyncUtils.supplyAsync
                (() -> baseMapper.listRepliesCounts(commentIdList)
                        .stream()
                        .collect(Collectors.toMap
                                (ReplyCountDTO::getCommentId, ReplyCountDTO::getReplyCount)));
        // 查询评论回复数据
        List<ReplyDTO> replyList = baseMapper.listReplies(commentIdList);
        // 获取评论点赞数据查询结果
        Map<String, Object> commentIdLikesCountMap
                = AsyncUtils.get(likeFuture, "查询评论点赞数据异常");
        // 封装回复点赞量
        replyList.forEach(replyDTO -> replyDTO.setLikeCount
                ((Integer) commentIdLikesCountMap.get(replyDTO.getId().toString())));
        // 根据评论 ID 分组回复数据
        Map<Integer, List<ReplyDTO>> commentIdRepliesMap = replyList
                .stream()
                .collect(Collectors.groupingBy(ReplyDTO::getParentId));
        // 获取根据评论 ID 查询回复量的结果
        Map<Integer, Integer> commentIdRepliesCountMap
                = AsyncUtils.get(replayCountFuture, "根据评论 ID 查询回复量异常");
        // 封装评论数据
        for (CommentDTO comment : commentList) {
            Integer commentId = comment.getId();
            // 封装点赞量
            comment.setLikeCount((Integer) commentIdLikesCountMap.get(commentId.toString()));
            // 封装回复量
            comment.setReplyCount(commentIdRepliesCountMap.get(commentId));
            // 分装评论数据
            comment.setReplyDTOList(commentIdRepliesMap.get(commentId));
        }
        return PageUtils.build(commentList, commentsCount);
    }

    @Override
    public List<ReplyDTO> listRepliesUnderComment(Integer commentId) {
        // 查询评论点赞数据
        CompletableFuture<Map<String, Object>> likeFuture
                = AsyncUtils.supplyAsync(() -> RedisUtils.hGetAll(COMMENT_LIKE_COUNT));
        // 查询评论下的回复数据
        List<ReplyDTO> replyList = baseMapper.listRepliesUnderComment
                (PageUtils.offset(), PageUtils.size(), commentId);
        // 获取评论点赞数查询结果
        Map<String, Object> replyIdLikeCountMap
                = AsyncUtils.get(likeFuture, "查询评论点赞数据异常");
        // 封装回复的点赞数
        replyList.forEach(reply -> reply.setLikeCount
                ((Integer) replyIdLikeCountMap.get(reply.getId().toString())));
        return replyList;
    }

    /**
     * 通知用户有新评论或通知博主审核评论
     */
    private void notify(Comment comment) {
        // 发送通知邮件
        if (comment.getIsReview().equals(FALSE_OF_INT)) {
            // 未审核则发送给博主审核提醒
            EmailDTO emailDTO = new EmailDTO();
            String bloggerEmail = ConfigUtils.getCache(WebsiteConfig::getEmail);
            emailDTO.setEmail(bloggerEmail);
            emailDTO.setSubject(String.format("博客审核提醒: %s",
                    TimeUtils.format(TimeUtils.now())));
            emailDTO.setContent("您收到一条新的回复，请前往后台管理页面审核。");
            RabbitUtils.sendEmail(emailDTO);
            return;
        }
        // 已审核或者不需要审核则发送给用户评论通知
        notifyUser(comment);
    }

    /**
     * 通知用户有新评论
     */
    private void notifyUser(Comment comment) {
        // 获取要通知用户的信息 ID
        EmailDTO emailDTO = new EmailDTO();
        Integer userInfoId = comment.getReplyUserId();
        if (userInfoId == null) {
            // 前端如果没有发送 ID，则从数据库查询
            Integer commentType = comment.getType();
            CommentTypeEnum typeEnum = CommentTypeEnum.get(commentType);
            Integer topicId = comment.getTopicId();
            if (ARTICLE.equals(typeEnum)) {
                userInfoId = articleMapper.selectById(topicId).getUserId();
            } else if (TALK.equals(typeEnum)) {
                userInfoId = talkMapper.selectById(topicId).getUserId();
            }
        }
        // 检查是否绑定邮箱，没有绑定无法发送
        String email = userInfoMapper.selectById(userInfoId).getEmail();
        if (StringUtils.isBlank(email)) {
            return;
        }
        emailDTO.setEmail(email);
        emailDTO.setSubject(String.format("博客评论提醒: %s",
                TimeUtils.format(TimeUtils.now())));
        // 获取查看评论的 URL
        String websiteUrl = ConfigUtils.getCache(WebsiteConfig::getUrl);
        String topicId = comment.getTopicId() == null ?
                EMPTY_STR : comment.getTopicId().toString();
        Integer commentType = comment.getType();
        String url = websiteUrl + CommentTypeEnum.getCommentPath(commentType) + topicId;
        emailDTO.setContent("您收到一条新的回复，请前往 " + url + " 页面查看。");
        RabbitUtils.sendEmail(emailDTO);
    }

}




