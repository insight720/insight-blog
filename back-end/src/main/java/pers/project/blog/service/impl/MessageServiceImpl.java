package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.blog.entity.MessageEntity;
import pers.project.blog.mapper.MessageMapper;
import pers.project.blog.service.MessageService;

/**
 * 针对表【tb_message】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageEntity> implements MessageService {

}




