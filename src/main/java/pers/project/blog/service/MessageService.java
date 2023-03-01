package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.message.AdminMessageDTO;
import pers.project.blog.dto.message.MessageDTO;
import pers.project.blog.entity.Message;
import pers.project.blog.vo.comment.ReviewVO;
import pers.project.blog.vo.message.MessageSearchVO;
import pers.project.blog.vo.message.MessageVO;

import java.util.List;

/**
 * 针对表【tb_message】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
public interface MessageService extends IService<Message> {

    /**
     * 添加留言弹幕
     */
    void saveMessage(MessageVO messageVO);

    /**
     * 删除留言
     */
    void removeMessages(List<Integer> messageIdList);

    /**
     * 审核留言
     */
    void updateMessagesReview(ReviewVO reviewVO);

    /**
     * 获取分页的后台留言列表数据
     */
    PageDTO<AdminMessageDTO> listAdminMessages(MessageSearchVO messageSearchVO);

    /**
     * 获取前台留言弹幕数据
     */
    List<MessageDTO> listMessages();

}
