package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.dreamlu.mica.core.exception.ServiceException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.constant.FilePathConst;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.talk.AdminTalkDTO;
import pers.project.blog.dto.talk.CommentCountDTO;
import pers.project.blog.dto.talk.TalkDTO;
import pers.project.blog.entity.Talk;
import pers.project.blog.enums.TalkStatusEnum;
import pers.project.blog.mapper.CommentMapper;
import pers.project.blog.mapper.TalkMapper;
import pers.project.blog.service.TalkService;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.util.*;
import pers.project.blog.vo.talk.TalkVO;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static pers.project.blog.constant.CacheConst.TALK;
import static pers.project.blog.constant.DatabaseConst.LIMIT_10;
import static pers.project.blog.constant.GenericConst.*;
import static pers.project.blog.constant.RedisConst.TALK_LIKE_COUNT;
import static pers.project.blog.constant.RedisConst.TALK_LIKE_PREFIX;

/**
 * 针对表【tb_talk】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @version 2023-01-11
 */
@Service
public class TalkServiceImpl extends ServiceImpl<TalkMapper, Talk> implements TalkService {

    @Resource
    private CommentMapper commentMapper;

    @Override
    public String saveTalkImages(MultipartFile multipartFile) {
        return UploadContext.executeStrategy
                (multipartFile, FilePathConst.TALK_DIR);
    }

    @Override
    @CacheEvict(cacheNames = TALK, allEntries = true)
    public void saveOrUpdateTalk(TalkVO talkVO) {
        Talk talk = BeanCopierUtils.copy(talkVO, Talk.class);
        talk.setUserId(SecurityUtils.getUserInfoId());
        saveOrUpdate(talk);
    }

    @Override
    @CacheEvict(cacheNames = TALK, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void removeTalks(List<Integer> talkIdList) {
        removeBatchByIds(talkIdList);
    }

    @Override
    public PageDTO<AdminTalkDTO> listAdminTalks(Integer status) {
        // 查询符合条件的说说总量
        Long adminTalkCount = lambdaQuery()
                .eq(Objects.nonNull(status),
                        Talk::getStatus, status)
                .count();
        if (adminTalkCount.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        // 查询符合条件的分页说说列表
        List<AdminTalkDTO> adminTalkList = baseMapper.listAdminTalks
                (PageUtils.offset(), PageUtils.size(), status);
        // 转换图片 URL 格式（JSON 数组转为 List）
        for (AdminTalkDTO adminTalk : adminTalkList) {
            adminTalk.setImgList(convertImages(adminTalk.getImages()));
        }
        return PageUtils.build(adminTalkList, adminTalkCount);
    }

    @Override
    public AdminTalkDTO getAdminTalk(Integer talkId) {
        AdminTalkDTO adminTalk = baseMapper.getAdminTalk(talkId);
        // 转换图片 URL 格式（JSON 数组转为 List）
        adminTalk.setImgList(convertImages(adminTalk.getImages()));
        return adminTalk;
    }

    @Override
    @Cacheable(cacheNames = TALK, key = "#root.methodName", sync = true)
    public List<String> listHomePageTalks() {
        // 查询最新的 10 条说说信息
        return lambdaQuery()
                .select(Talk::getContent)
                .eq(Talk::getStatus, TalkStatusEnum.PUBLIC.getStatus())
                .orderByDesc(Talk::getIsTop)
                .orderByDesc(Talk::getId)
                .last(LIMIT_10)
                .list()
                .stream()
                .map(Talk::getContent)
                // 处理说说内容（删除标签，保留最多 200 字符）
                .map(content -> StrRegexUtils.deleteTag(content.length() > TWO_HUNDRED ?
                        content.substring(ZERO, TWO_HUNDRED) : content))
                .collect(Collectors.toList());
    }

    @Override
    public PageDTO<TalkDTO> listTalks() {
        // 查询公开的说说总量
        Long talkCount = lambdaQuery()
                .eq(Talk::getStatus, TalkStatusEnum.PUBLIC.getStatus())
                .count();
        if (talkCount.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        // 查询说说点赞量
        CompletableFuture<Map<String, Object>> future = AsyncUtils.supplyAsync
                (() -> RedisUtils.hGetAll(TALK_LIKE_COUNT));
        // 查询分页的公开说说数据
        List<TalkDTO> talkList = baseMapper.listTalks
                (PageUtils.offset(), PageUtils.size());
        // 查询说说评论量
        List<Integer> talkIdList = talkList.stream()
                .map(TalkDTO::getId)
                .collect(Collectors.toList());
        // 获取评论主题 ID 和评论数量的映射
        Map<Integer, Integer> topicIdCommentsCountMap
                = commentMapper.getTopicIdAndCommentsCount(talkIdList)
                .stream()
                .collect(Collectors.toMap
                        (CommentCountDTO::getTopicId, CommentCountDTO::getCommentCount));
        // 获取评论评论 ID 和点赞量的映射
        Map<String, Object> topicIdLikesCountMap
                = AsyncUtils.get(future, "查询说说点赞量异常");
        // 组装说说数据
        for (TalkDTO talk : talkList) {
            Integer topicId = talk.getId();
            talk.setCommentCount(topicIdCommentsCountMap.get(topicId));
            talk.setLikeCount((Integer) topicIdLikesCountMap.get(topicId.toString()));
            // 转换图片 URL 格式（JSON 数组转为 List）
            talk.setImgList(convertImages(talk.getImages()));
        }
        return PageUtils.build(talkList, talkCount);
    }

    @Override
    public TalkDTO getTalk(Integer talkId) {
        // 查询说说信息
        TalkDTO talk = Optional
                .ofNullable(baseMapper.getTalk(talkId))
                .orElseThrow(() -> new ServiceException("说说不存在"));
        // 查询说说点赞量
        Integer likesCount = (Integer) RedisUtils.hGet
                (TALK_LIKE_COUNT, talkId.toString());
        talk.setLikeCount(likesCount);
        // 转换图片 URL 格式（JSON 数组转为 List）
        talk.setImgList(convertImages(talk.getImages()));
        return talk;
    }

    @Override
    public void likeTalk(Integer talkId) {
        // 判断是否点过赞来区分点赞和取消赞
        RedisUtils.likeOrUnlike(TALK_LIKE_PREFIX, TALK_LIKE_COUNT, talkId);
    }

    /**
     * 转换图片 URL 格式（JSON 数组转为 List）
     */
    private List<String> convertImages(String images) {
        if (StrRegexUtils.isBlank(images)) {
            return null;
        }
        return ConvertUtils.castList(ConvertUtils.parseJson(images, List.class));
    }

}




