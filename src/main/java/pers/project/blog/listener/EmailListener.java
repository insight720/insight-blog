package pers.project.blog.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.RabbitConst;
import pers.project.blog.dto.comment.EmailDTO;
import pers.project.blog.util.ConvertUtils;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 邮件监听器
 *
 * @author Luo Fei
 * @version 2023/1/16
 */
@Component
@RabbitListener(queues = RabbitConst.EMAIL_QUEUE)
public class EmailListener {

    @Value("${spring.mail.username}")
    private String email;

    @Resource
    private JavaMailSender javaMailSender;


    @RabbitHandler
    public void process(byte[] jsonBytes, Channel channel, Message message) throws IOException {
        EmailDTO emailDTO = ConvertUtils.parseJsonBytes(jsonBytes, EmailDTO.class);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(email);
        mailMessage.setTo(emailDTO.getEmail());
        mailMessage.setSubject(emailDTO.getSubject());
        mailMessage.setText(emailDTO.getContent());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            javaMailSender.send(mailMessage);
        } catch (MailException cause) {
            channel.basicReject(deliveryTag, true);
            throw new RuntimeException("邮件发送异常", cause);
        }
        channel.basicAck(deliveryTag, false);
    }

}
