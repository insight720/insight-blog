package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.AdminMessageDTO;
import pers.project.blog.dto.MessageDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.entity.MessageEntity;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.MessageVO;
import pers.project.blog.vo.ReviewVO;

import java.util.List;

/**
 * 针对表【tb_message】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
public interface MessageService extends IService<MessageEntity> {

    /**
     * 查看后台的分页留言列表
     *
     * @param conditionVO 条件
     * @return {@code PageDTO<AdminMessageDTO>} 留言列表
     */
    PageDTO<AdminMessageDTO> listAdminMessages(ConditionVO conditionVO);

    /**
     * 审核留言
     *
     * @param reviewVO 审核信息
     */
    void updateMessagesReview(ReviewVO reviewVO);

    /**
     * 查看留言弹幕
     *
     * @return {@code List<MessageDTO>} 留言列表
     */
    List<MessageDTO> listMessages();

    /**
     * 添加留言弹幕
     *
     * @param messageVO 留言数据
     */
    void saveMessage(MessageVO messageVO);

}
