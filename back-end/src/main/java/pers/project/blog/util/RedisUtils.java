package pers.project.blog.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Redis 数据访问工具类
 *
 * @author Luo Fei
 * @date 2022/12/25
 */
public abstract class RedisUtils {

    private static RedisTemplate<String, Object> redisTemplate;

    /**
     * 为 Redis 工具类设置 {@link RedisTemplate}
     *
     * @param redisTemplate 配置好的 {@link RedisTemplate}
     */
    public static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }

    /**
     * 获取 Set 结构
     *
     * @param key key
     * @return Set 集合
     */
    public static Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 是否为 Set 结构中的成员
     *
     * @param key    key
     * @param member 可能的成员
     * @return 是否存在
     */
    public static Boolean sIsMember(String key, Object member) {
        return redisTemplate.opsForSet().isMember(key, member);
    }

    /**
     * Hash 结构中字段按增量增加
     *
     * @param key       key
     * @param field     字段
     * @param increment 增量
     * @return 返回增加后的数据
     */
    public static Long hIncrBy(String key, String field, long increment) {
        return redisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * String 结构的值按增量增加
     *
     * @param key       key
     * @param increment 增量
     * @return 返回增加后结果
     */
    public static Long incrBy(String key, long increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 向 Set 结构中添加成员
     *
     * @param key     key
     * @param members 成员
     * @return 返回增加数量
     */
    public static Long sAdd(String key, Object... members) {
        return redisTemplate.opsForSet().add(key, members);
    }

    /**
     * 获取 String 结构的值
     *
     * @param key key
     * @return 值
     */
    public static Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 返回 Hash 结构中，所有的域和值
     *
     * @param key key
     * @return 域和值的映射
     */
    @NotNull
    public static Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * ZSet 结构获取指定元素和分数的映射
     *
     * @param key   key
     * @param start 开始
     * @param end   结束
     * @return 元素和分数的映射，<b>按分数降序排序</b>
     */
    @NotNull
    public static Map<Object, Double> zRevRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end)
                .stream()
                .collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue,
                        ZSetOperations.TypedTuple::getScore,
                        (score, anotherScore) -> score, LinkedHashMap::new));
    }

    /**
     * String 结构为 key 设置 value
     *
     * @param key   键，不能为空
     * @param value 值，不能为空
     */
    public static void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 删除给定键对应的值
     *
     * @param key 键，不能为空
     */
    public static void del(String key) {
        redisTemplate.delete(key);
    }

}
