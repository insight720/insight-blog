package pers.project.blog.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
     * 为哈希表 key 中的域 field 的值加上增量 increment。
     * <p>
     * 增量也可以为负数，相当于对给定域进行减法操作。
     *
     * @param key       key
     * @param field     域
     * @param increment 增量
     * @return 返回增加后的数据
     */
    public static Long hIncrBy(String key, String field, long increment) {
        return redisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * 为字符串键 key 储存的数字值加上增量 increment 。
     * <p>
     * 如果键 key 不存在， 那么键 key 的值会先被初始化为 0 ， 然后再执行 INCRBY 命令。
     *
     * @param key       key
     * @param increment 增量
     * @return {@code Long} 在加上增量 increment 之后， 键 key 当前的值。
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
    public static Map<String, Object> hGetAll(String key) {
        Map fieldValueMap = redisTemplate.opsForHash().entries(key);
        return fieldValueMap;
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
    public static Map<Object, Double> zRevRangeWithScores(@NotNull String key, long start, long end) {
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
     * 将键 key 的值设置为 value ， 并将键 key 的生存时间设置为 seconds 秒钟。
     * <p>
     * 如果键 key 已经存在， 那么 SETEX 命令将覆盖已有的值。
     * <p>
     * SETEX 的不同之处在于 SETEX 是一个原子（atomic）操作，
     * 它可以在同一时间内完成设置值和设置过期时间这两个操作， 因此 SETEX 命令在储存缓存的时候非常实用。
     *
     * @param key   键，不能为空
     * @param value 值，不能为空
     */
    public static void setEx(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 删除给定键对应的值
     *
     * @param key 键，不能为空
     */
    public static void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。
     * <p>
     * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，
     * 以 1 表示有序集第二个成员，以此类推。 你也可以使用负数下标，以 -1 表示最后
     * 一个成员， -2 表示倒数第二个成员，以此类推。
     * <p>
     * 超出范围的下标并不会引起错误。 比如说，当 start 的值比有序集的最大下标还要
     * 大，或是 start > stop 时， ZRANGE 命令只是简单地返回一个空列表。 另一方
     * 面，假如 stop 参数的值比有序集的最大下标还要大，那么 Redis 将 stop 当作
     * 最大下标来处理。
     *
     * @param key   键，不能为空
     * @param start 起始下标
     * @param end   终止下标
     * @return 指定区间内，有序集成员与其 score 值的映射，<b>按分数降序排序</b>。
     */
    @NotNull
    public static Map<Object, Double> zRangeWithScores(@NotNull String key, int start, int end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end)
                .stream()
                .collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue,
                        ZSetOperations.TypedTuple::getScore,
                        (score, anotherScore) -> score, LinkedHashMap::new));
    }

    /**
     * 返回哈希表给定域的值。
     * <p>
     * 如果给定域不存在于哈希表中， 又或者给定的哈希表并不存在， 那么命令返回 null。
     *
     * @param key     给定哈希表
     * @param hashKey 给定域
     * @return {@code Object} 哈希表给定域的值
     */
    @Nullable
    public static Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
     * <p>
     * 当 key 不是集合类型，返回一个错误。
     *
     * @param key     集合 key
     * @param members 一个或多个 member 元素
     * @return {@code Long} 被成功移除的元素的数量，不包括被忽略的元素。
     */
    public static Long sRem(String key, Object... members) {
        return redisTemplate.opsForSet().remove(key, members);
    }

    /**
     * 为有序集 key 的成员 member 的 score 值加上增量 increment 。
     * <p>
     * 可以通过传递一个负数值 increment ，让 score 减去相应的值，比如 ZINCRBY key -5 member ，就是让 member 的 score 值减去 5 。
     * <p>
     * 当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key increment member 等同于 ZADD key increment member 。
     * <p>
     * 当 key 不是有序集类型时，返回一个错误。
     * <p>
     * score 值可以是整数值或双精度浮点数。
     *
     * @param key       键
     * @param member    成员
     * @param increment 增量
     * @return {@code Double} member 成员的新 score 值。
     */
    public static Double zIncyBy(String key, Object member, double increment) {
        return redisTemplate.opsForZSet().incrementScore(key, member, increment);
    }

    /**
     * 返回有序集 key 中，成员 member 的 score 值。
     * <p>
     * 如果 member 元素不是有序集 key 的成员，或 key 不存在，返回 null 。
     *
     * @param key    键
     * @param member 成员
     * @return {@code Double} 成员 member 的 score 值
     */
    public static Double zScore(String key, Object member) {
        return redisTemplate.opsForZSet().score(key, member);
    }

    /**
     * 执行 {@link RedisUtils#incrBy(String, long)} 操作，并为字符串 key 设置过期时间，
     * 这个操作不会刷新过期时间。
     *
     * @param key       字符串键
     * @param increment 增量，不可以为负数
     * @param timeout   过期时间量
     * @param timeunit  时间单位
     * @return {@code Long} 在加上增量 increment 之后， 键 key 当前的值。
     */
    public static Long incrByAndExpire(String key, long increment, long timeout, TimeUnit timeunit) {
        Long count = incrBy(key, increment);
        if (count == increment) {
            redisTemplate.expire(key, timeout, timeunit);
        }
        return count;
    }

    /**
     * 返回集合 key 中元素的数量。
     *
     * @param key 集合
     * @return {@code Long} 集合中元素的数量。当 key 不存在时，返回 0。
     */
    public static Long sCard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

}
