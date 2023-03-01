package pers.project.blog.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.RabbitConst;
import pers.project.blog.dto.article.ArticleSearchDTO;
import pers.project.blog.dto.article.MaxwellDataDTO;
import pers.project.blog.entity.Article;
import pers.project.blog.util.ConvertUtils;

import javax.annotation.Resource;
import java.io.IOException;

import static pers.project.blog.constant.MaxWellConst.*;

/**
 * Maxwell 监听器
 *
 * @author Luo Fei
 * @date 2023/1/16
 */
@Component
@RabbitListener(queues = RabbitConst.MAXWELL_QUEUE)
public class MaxwellListener {

    @Resource
    private ArticleRepository articleRepository;

    @RabbitHandler
    public void process(byte[] jsonBytes, Channel channel, Message message) throws IOException {
        // 获取监听信息
        MaxwellDataDTO maxwellDataDTO
                = ConvertUtils.parseJsonBytes(jsonBytes, MaxwellDataDTO.class);
        // 获取文章数据
        Article article = ConvertUtils.parseJson
                (ConvertUtils.getJson(maxwellDataDTO.getData()), Article.class);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // 判断操作类型，进行操作
            String type = maxwellDataDTO.getType();
            if (type.equals(INSERT) || type.equals(UPDATE)) {
                articleRepository.save(ConvertUtils.convert(article, ArticleSearchDTO.class));
            } else if (type.equals(DELETE)) {
                articleRepository.deleteById(article.getId());
            }
        } catch (Exception cause) {
            channel.basicReject(deliveryTag, true);
            throw new RuntimeException("ES 文章数据操作异常", cause);
        }
        channel.basicAck(deliveryTag, false);
    }

}


