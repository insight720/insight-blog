package pers.project.blog.util;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.EmailDTO;

import static pers.project.blog.constant.RabbitConstant.EMAIL_EXCHANGE;

/**
 * RabbitMq 工具类
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Component
public final class RabbitUtils {

    private static RabbitTemplate RABBIT_TEMPLATE;

    private RabbitUtils() {
    }

    public static void sendEmail(EmailDTO emailDTO) {
        RABBIT_TEMPLATE.convertAndSend(EMAIL_EXCHANGE, "*",
                new Message(ConversionUtils.getJson(emailDTO).getBytes(), new MessageProperties()));
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        RabbitUtils.RABBIT_TEMPLATE = rabbitTemplate;
    }

}
