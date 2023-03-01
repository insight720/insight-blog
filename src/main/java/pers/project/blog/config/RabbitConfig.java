package pers.project.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static pers.project.blog.constant.RabbitConst.*;

/**
 * RabbitMQ 配置类
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
@Slf4j
@Configuration
public class RabbitConfig {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void configureRabbitTemplate() {
        // 消息发送给 Exchange 的回调
        RabbitTemplate.ConfirmCallback confirmCallback
                = (CorrelationData correlationData, boolean ack, String cause) -> {
            if (log.isDebugEnabled()) {
                log.debug("{} ack = {} cause = {}", correlationData, ack, cause);
            }
            if (!ack) {
                String message = String.format("消息发送给 Exchange 失败: %s Cause [%s]",
                        correlationData, cause);
                throw new RuntimeException(message);
            }
        };
        rabbitTemplate.setConfirmCallback(confirmCallback);
        // 消息发送给 Queue 的失败回调
        RabbitTemplate.ReturnsCallback returnsCallback = (ReturnedMessage returned) -> {
            String message = String.format("消息发送给 Queue 失败: %s", returned);
            throw new RuntimeException(message);
        };
        rabbitTemplate.setReturnsCallback(returnsCallback);
    }

    @Bean
    public Queue maxWellQueue() {
        return QueueBuilder.durable(MAXWELL_QUEUE).build();
    }

    @Bean
    public FanoutExchange maxWellExchange() {
        return ExchangeBuilder.fanoutExchange(MAXWELL_EXCHANGE).build();
    }

    @Bean
    public Binding maxWellBinding() {
        return BindingBuilder.bind(maxWellQueue()).to(maxWellExchange());
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE).build();
    }

    @Bean
    public FanoutExchange emailExchange() {
        return ExchangeBuilder.fanoutExchange(EMAIL_EXCHANGE).build();
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange());
    }

}
