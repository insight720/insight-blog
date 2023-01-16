package pers.project.blog.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.RabbitConstant;
import pers.project.blog.dto.EmailDTO;
import pers.project.blog.util.ConversionUtils;

/**
 * 邮件监听器
 *
 * @author Luo Fei
 * @date 2023/1/16
 */
@Component
@RabbitListener(queues = RabbitConstant.EMAIL_QUEUE)
public class EmailListener {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String email;

    public EmailListener(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // TODO: 2023/1/16 没开确认，可能会丢消息
    @RabbitHandler
    public void process(byte[] data) {
        EmailDTO emailDTO = ConversionUtils.parseJson(new String(data), EmailDTO.class);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(email);
        mailMessage.setTo(emailDTO.getEmail());
        mailMessage.setSubject(emailDTO.getSubject());
        mailMessage.setText(emailDTO.getContent());
        javaMailSender.send(mailMessage);
    }

}
