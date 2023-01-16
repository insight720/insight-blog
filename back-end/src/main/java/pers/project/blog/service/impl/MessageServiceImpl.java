package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pers.project.blog.constant.BooleanConstant;
import pers.project.blog.dto.AdminMessageDTO;
import pers.project.blog.dto.MessageDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.entity.MessageEntity;
import pers.project.blog.mapper.MessageMapper;
import pers.project.blog.service.BlogInfoService;
import pers.project.blog.service.MessageService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.IpUtils;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.util.SecurityUtils;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.MessageVO;
import pers.project.blog.vo.ReviewVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 针对表【tb_message】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageEntity> implements MessageService {

    private final BlogInfoService blogInfoService;

    private final HttpServletRequest httpServletRequest;

    public MessageServiceImpl(BlogInfoService blogInfoService, HttpServletRequest httpServletRequest) {
        this.blogInfoService = blogInfoService;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public PageDTO<AdminMessageDTO> listAdminMessages(ConditionVO conditionVO) {
        // 按条件分页查询留言列表
        String keywords = conditionVO.getKeywords();
        Integer isReview = conditionVO.getIsReview();
        IPage<MessageEntity> page = lambdaQuery()
                .eq(Objects.nonNull(isReview), MessageEntity::getIsReview, isReview)
                .like(StringUtils.isNotBlank(keywords), MessageEntity::getNickname, keywords)
                .orderByDesc(MessageEntity::getId)
                .page(PaginationUtils.getPage());

        List<AdminMessageDTO> adminMessageList
                = ConversionUtils.covertList(page.getRecords(), AdminMessageDTO.class);
        return PageDTO.of(adminMessageList, (int) page.getTotal());
    }

    @Override
    public void updateMessagesReview(ReviewVO reviewVO) {
        Integer isReview = reviewVO.getIsReview();
        List<MessageEntity> messageEntityList = reviewVO.getIdList()
                .stream()
                .map(messageId -> MessageEntity.builder()
                        .id(messageId)
                        .isReview(isReview)
                        .build())
                .collect(Collectors.toList());
        updateBatchById(messageEntityList);
    }

    @Override
    public List<MessageDTO> listMessages() {
        List<MessageEntity> messageList = lambdaQuery()
                .select(MessageEntity::getId,
                        MessageEntity::getNickname,
                        MessageEntity::getAvatar,
                        MessageEntity::getMessageContent,
                        MessageEntity::getTime)
                .eq(MessageEntity::getIsReview, BooleanConstant.TRUE)
                .list();
        return ConversionUtils.covertList(messageList, MessageDTO.class);
    }

    @Override
    public void saveMessage(MessageVO messageVO) {
        // 判断是否需要审核
        Integer isMessageReview = blogInfoService.getWebSiteConfig().getIsMessageReview();

        // 获取用户 IP
        String ipAddress = IpUtils.getIpAddress(httpServletRequest);
        String ipSource = IpUtils.getIpSource(ipAddress);

        // 封装数据并保存
        MessageEntity messageEntity = ConversionUtils
                .convertObject(messageVO, MessageEntity.class);
        messageEntity.setMessageContent
                (SecurityUtils.filter(messageEntity.getMessageContent()));
        messageEntity.setIpAddress(ipAddress);
        messageEntity.setIpSource(ipSource);
        messageEntity.setIsReview(isMessageReview.equals(BooleanConstant.TRUE) ?
                BooleanConstant.FALSE : BooleanConstant.TRUE);
        save(messageEntity);
    }

}




