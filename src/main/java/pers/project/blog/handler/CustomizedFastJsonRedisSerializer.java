package pers.project.blog.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.ContextAutoTypeBeforeHandler;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.data.redis.GenericFastJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import pers.project.blog.security.BlogUserDetails;

/**
 * 定制的 FastJson Redis 序列化器
 * <p>
 * 仅添加了 Feature.FieldBased 一项配置，基于字段而不是方法进行序列化，
 * 解决 {@link BlogUserDetails} 中基于方法序列化存在的问题。
 *
 * @author Luo Fei
 * @version 2023/3/19
 * @see GenericFastJsonRedisSerializer
 */
public class CustomizedFastJsonRedisSerializer implements RedisSerializer<Object> {

    private final FastJsonConfig config = new FastJsonConfig();

    public CustomizedFastJsonRedisSerializer() {
        // 相较于 GenericFastJsonRedisSerializer，只添加了 Feature.FieldBased
        config.setReaderFeatures(JSONReader.Feature.SupportAutoType, JSONReader.Feature.FieldBased);
        config.setWriterFeatures(JSONWriter.Feature.WriteClassName, JSONWriter.Feature.FieldBased);
    }

    public CustomizedFastJsonRedisSerializer(String[] acceptNames, boolean jsonb) {
        this();
        config.setReaderFilters(new ContextAutoTypeBeforeHandler(acceptNames));
        config.setJSONB(jsonb);
    }

    public CustomizedFastJsonRedisSerializer(String[] acceptNames) {
        this(acceptNames, false);
    }

    public CustomizedFastJsonRedisSerializer(boolean jsonb) {
        this(new String[0], jsonb);
    }

    @Override
    public byte[] serialize(Object object) throws SerializationException {
        if (object == null) {
            return new byte[0];
        }
        try {
            if (config.isJSONB()) {
                return JSONB.toBytes(object, config.getWriterFeatures());
            } else {
                return JSON.toJSONBytes(object, config.getWriterFeatures());
            }
        } catch (Exception ex) {
            throw new SerializationException("Could not serialize: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            if (config.isJSONB()) {
                return JSONB.parseObject(bytes, Object.class, null, config.getReaderFilters(), config.getReaderFeatures());
            } else {
                return JSON.parseObject(bytes, Object.class, null, config.getReaderFilters(), config.getReaderFeatures());
            }
        } catch (Exception ex) {
            throw new SerializationException("Could not deserialize: " + ex.getMessage(), ex);
        }
    }

}

