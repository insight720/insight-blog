package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.blog.constant.RedisConstant;
import pers.project.blog.constant.enumeration.TalkStatusEnum;
import pers.project.blog.dto.AdminTalkDTO;
import pers.project.blog.dto.CommentCountDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.TalkDTO;
import pers.project.blog.entity.TalkEntity;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.CommentMapper;
import pers.project.blog.mapper.TalkMapper;
import pers.project.blog.service.TalkService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.util.SecurityUtils;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.TalkVO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 针对表【tb_talk】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-11
 */
@Service
@SuppressWarnings("unchecked")
public class TalkServiceImpl extends ServiceImpl<TalkMapper, TalkEntity> implements TalkService {

    private final CommentMapper commentMapper;

    public TalkServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public PageDTO<AdminTalkDTO> listAdminTalks(ConditionVO conditionVO) {
        // 查询符合条件的说说总量
        Long adminTalkCount = lambdaQuery()
                .eq(Objects.nonNull(conditionVO.getStatus()),
                        TalkEntity::getStatus, conditionVO.getStatus())
                .count();
        if (adminTalkCount == 0) {
            return new PageDTO<>();
        }

        // 查询符合条件的分页说说列表
        IPage<TalkEntity> page = PaginationUtils.getPage();
        List<AdminTalkDTO> adminTalkList = baseMapper.listAdminTalkDTOs
                (page.offset(), page.getSize(), conditionVO);
        // TODO: 2023/1/11 数据库似乎没存 imgList
        // 转换格式（JSON 转为 List）
        adminTalkList.forEach(adminTalk -> adminTalk.setImgList
                (ConversionUtils.parseJson(adminTalk.getImages(), List.class)));

        return PageDTO.of(adminTalkList, adminTalkCount.intValue());
    }

    @Override
    public void saveOrUpdateTalk(TalkVO talkVO) {
        TalkEntity talkEntity = ConversionUtils.convertObject(talkVO, TalkEntity.class);
        talkEntity.setUserId(SecurityUtils.getUserDetails().getUserInfoId());
        saveOrUpdate(talkEntity);
    }

    @Override
    public AdminTalkDTO getAdminTalk(Integer talkId) {
        AdminTalkDTO adminTalkDTO = baseMapper.getAdminTalkDTO(talkId);
        // 转换格式（JSON 转为 List）
        adminTalkDTO.setImgList(ConversionUtils.parseJson(adminTalkDTO.getImages(), List.class));
        return adminTalkDTO;
    }

    @Override
    public List<String> listHomePageTalks() {
        // 查询最新的 10 条说说信息
        return lambdaQuery()
                .eq(TalkEntity::getStatus, TalkStatusEnum.PUBLIC.getStatus())
                .orderByDesc(TalkEntity::getIsTop)
                .orderByDesc(TalkEntity::getId)
                .last("LIMIT 10")
                .list()
                .stream()
                .map(TalkEntity::getContent)
                // TODO: 2023/1/11 意义不明
                .map(content -> SecurityUtils.deleteHMTLTag(content.length() > 200 ?
                        content.substring(0, 200) : content))
                .collect(Collectors.toList());
    }

    @Override
    public PageDTO<TalkDTO> listTalks() {
        // 查询公开的说说总量
        Long talkCount = lambdaQuery()
                .eq(TalkEntity::getStatus, TalkStatusEnum.PUBLIC.getStatus())
                .count();
        if (talkCount == 0) {
            return new PageDTO<>();
        }

        // 查询分页的公开说说信息
        IPage<Object> page = PaginationUtils.getPage();
        List<TalkDTO> talkDTOList = baseMapper.listTalkDTOs(page.offset(), page.getSize());

        // 查询说说评论量和点赞量
        List<Integer> talkIdList = talkDTOList.stream()
                .map(TalkDTO::getId)
                .collect(Collectors.toList());
        Map<Integer, Integer> topicIdCommentsCountMap
                = commentMapper.getTopicIdCommentsCountMap(talkIdList)
                .stream()
                .collect(Collectors.toMap
                        (CommentCountDTO::getTopicId, CommentCountDTO::getCommentCount));

        Map<String, Object> topicIdLikesCountMap
                = RedisUtils.hGetAll(RedisConstant.TALK_LIKE_COUNT);
        talkDTOList.forEach(talkDTO -> {
            Integer topicId = talkDTO.getId();
            talkDTO.setCommentCount(topicIdCommentsCountMap.get(topicId));
            talkDTO.setLikeCount((Integer) topicIdLikesCountMap.get(topicId.toString()));
            // 转换格式（JSON 转为 List）
            talkDTO.setImgList(ConversionUtils.parseJson(talkDTO.getImages(), List.class));
        });

        return PageDTO.of(talkDTOList, talkCount.intValue());
    }

    @Override
    public TalkDTO getTalk(Integer talkId) {
        // 查询说说信息
        TalkDTO talkDTO = Optional
                .ofNullable(baseMapper.getTalkDTO(talkId))
                .orElseThrow(() -> new ServiceException("说说不存在"));

        // 查询说说点赞量
        Integer likesCount = (Integer) RedisUtils.hGet(RedisConstant.TALK_LIKE_COUNT, talkId.toString());
        talkDTO.setLikeCount(likesCount);
        // 转换格式（JSON 转为 List）
        talkDTO.setImgList(ConversionUtils.parseJson(talkDTO.getImages(), List.class));

        return talkDTO;
    }

    @Override
    public void saveTalkLike(Integer talkId) {
        // 判断是否点过赞来区分点赞和取消赞
        String talkLikeKey = RedisConstant.TALK_USER_LIKE
                + SecurityUtils.getUserDetails().getUserInfoId();
        if (RedisUtils.sIsMember(talkLikeKey, talkId)) {
            RedisUtils.sRem(talkLikeKey, talkId);
            RedisUtils.hIncrBy(RedisConstant.TALK_LIKE_COUNT, talkId.toString(), -1L);
        } else {
            RedisUtils.sAdd(talkLikeKey, talkId);
            RedisUtils.hIncrBy(RedisConstant.TALK_LIKE_COUNT, talkId.toString(), 1L);
        }
    }

}




