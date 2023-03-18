package pers.project.blog.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.comment.EmailDTO;

import static pers.project.blog.constant.RabbitConst.EMAIL_EXCHANGE;
import static pers.project.blog.constant.RabbitConst.ROUTING_KEY;

/**
 * RabbitMq 工具类
 *
 * @author Luo Fei
 * @version 2023/1/15
 */
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RabbitUtils {

    private static RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        RabbitUtils.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送邮件
     *
     * @param emailDTO 邮件数据
     */
    public static void sendEmail(EmailDTO emailDTO) {
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, ROUTING_KEY,
                new Message(ConvertUtils.getJsonBytes(emailDTO), new MessageProperties()));
    }

}
