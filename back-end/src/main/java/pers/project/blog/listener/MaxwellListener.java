package pers.project.blog.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.RabbitConstant;
import pers.project.blog.dto.ArticleSearchDTO;
import pers.project.blog.dto.MaxwellDataDTO;
import pers.project.blog.entity.ArticleEntity;
import pers.project.blog.mapper.elasticsearch.ArticleSearchMapper;
import pers.project.blog.util.ConversionUtils;


/**
 * Maxwell 监听器
 *
 * @author Luo Fei
 * @date 2023/1/16
 */
@Component
@RabbitListener(queues = RabbitConstant.MAXWELL_QUEUE)
public class MaxwellListener {

    private final ArticleSearchMapper articleSearchMapper;

    public MaxwellListener(ArticleSearchMapper articleSearchMapper) {
        this.articleSearchMapper = articleSearchMapper;
    }

    @RabbitHandler
    public void process(byte[] data) {
        // 获取监听信息
        MaxwellDataDTO maxwellDataDTO = ConversionUtils.parseJson
                (new String(data), MaxwellDataDTO.class);

        // 获取文章数据
        ArticleEntity articleEntity = ConversionUtils.parseJson
                (ConversionUtils.getJson(maxwellDataDTO.getData()), ArticleEntity.class);

        // 判断操作类型，进行操作
        switch (maxwellDataDTO.getType()) {
            case "insert":
            case "update":
                articleSearchMapper.save
                        (ConversionUtils.convertObject(articleEntity, ArticleSearchDTO.class));
                break;
            case "delete":
                articleSearchMapper.deleteById(articleEntity.getId());
                break;
        }
    }

}
