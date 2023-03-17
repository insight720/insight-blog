package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.bloginfo.WebsiteConfig;
import pers.project.blog.dto.message.AdminMessageDTO;
import pers.project.blog.dto.message.MessageDTO;
import pers.project.blog.entity.Message;
import pers.project.blog.mapper.MessageMapper;
import pers.project.blog.service.MessageService;
import pers.project.blog.util.*;
import pers.project.blog.vo.comment.ReviewVO;
import pers.project.blog.vo.message.MessageSearchVO;
import pers.project.blog.vo.message.MessageVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static pers.project.blog.constant.GenericConst.FALSE_OF_INT;
import static pers.project.blog.constant.GenericConst.TRUE_OF_INT;

/**
 * 针对表【tb_message】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public void saveMessage(MessageVO messageVO) {
        Message message = BeanCopierUtils.copy(messageVO, Message.class);
        // 判断是否需要审核
        Integer isMessageReview = ConfigUtils.getCache(WebsiteConfig::getIsMessageReview);
        message.setIsReview(isMessageReview.equals(TRUE_OF_INT) ?
                FALSE_OF_INT : TRUE_OF_INT);
        // 获取用户或游客的 IP 信息
        HttpServletRequest request = WebUtils.getCurrentRequest();
        String ipAddress = WebUtils.getIpAddress(request);
        message.setIpAddress(ipAddress);
        String ipSource = WebUtils.getIpSource(ipAddress);
        message.setIpSource(ipSource);
        // 过滤留言内容
        message.setMessageContent(StrRegexUtils.filter(message.getMessageContent()));
        // 保存留言
        save(message);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void removeMessages(List<Integer> messageIdList) {
        removeBatchByIds(messageIdList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateMessagesReview(ReviewVO reviewVO) {
        Integer isReview = reviewVO.getIsReview();
        List<Message> messageList = reviewVO.getIdList()
                .stream()
                .map(messageId -> Message.builder()
                        .id(messageId)
                        .isReview(isReview)
                        .build())
                .collect(Collectors.toList());
        updateBatchById(messageList);
    }

    @Override
    public PageDTO<AdminMessageDTO> listAdminMessages(MessageSearchVO messageSearchVO) {
        // 按条件分页查询留言列表
        String keywords = messageSearchVO.getKeywords();
        Integer isReview = messageSearchVO.getIsReview();
        IPage<Message> page = lambdaQuery()
                .eq(Objects.nonNull(isReview), Message::getIsReview, isReview)
                .like(StrRegexUtils.isNotBlank(keywords), Message::getNickname, keywords)
                .orderByDesc(Message::getId)
                .page(PageUtils.getPage());
        List<AdminMessageDTO> adminMessageList
                = ConvertUtils.convertList(page.getRecords(), AdminMessageDTO.class);
        return PageUtils.build(adminMessageList, page.getTotal());
    }

    @Override
    public List<MessageDTO> listMessages() {
        List<Message> messageList = lambdaQuery()
                .select(Message::getId,
                        Message::getNickname,
                        Message::getAvatar,
                        Message::getMessageContent,
                        Message::getTime)
                .eq(Message::getIsReview, TRUE_OF_INT)
                .list();
        return ConvertUtils.convertList(messageList, MessageDTO.class);
    }

}




