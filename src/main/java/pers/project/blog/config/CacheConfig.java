package pers.project.blog.config;

import com.alibaba.fastjson2.support.spring.data.redis.GenericFastJsonRedisSerializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import static pers.project.blog.constant.CacheConst.CACHE_NAME_PREFIX;

/**
 * 缓存配置类
 *
 * @author Luo Fei
 * @version 2023/1/31
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // 设置缓存名前缀
        config = config.prefixCacheNameWith(CACHE_NAME_PREFIX);
        // key 用 String 序列化
        config = config.serializeKeysWith(RedisSerializationContext
                .SerializationPair.fromSerializer(RedisSerializer.string()));
        // value 用 FastJson2 序列化
        config = config.serializeValuesWith(RedisSerializationContext
                .SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));
        return config;
    }

}
