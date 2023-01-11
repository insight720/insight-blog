package pers.project.blog.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
     * 为哈希表 key 中的域 field 的值加上增量 increment。
     * <p>
     * 增量也可以为负数，相当于对给定域进行减法操作。
     *
     * @param key       key
     * @param field     域
     * @param increment 增量
     * @return 返回增加后的数据
     */
    public static Long hIncrBy(String key, Object field, long increment) {
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
    public static Object hGet(String key, Integer hashKey) {
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

}
