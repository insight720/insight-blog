package pers.project.blog.configuration;

import com.alibaba.fastjson2.support.spring.data.redis.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import pers.project.blog.util.RedisUtils;

/**
 * Redis 配置类
 *
 * @author Luo Fei
 * @date 2022/12/25
 */
@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // key 用 String 序列化
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());

        // value 用 FastJson2 序列化
        GenericFastJsonRedisSerializer genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
        template.setValueSerializer(genericFastJsonRedisSerializer);
        template.setHashValueSerializer(genericFastJsonRedisSerializer);

        template.setConnectionFactory(redisConnectionFactory);

        RedisUtils.setRedisTemplate(template);

        return template;
    }

}
