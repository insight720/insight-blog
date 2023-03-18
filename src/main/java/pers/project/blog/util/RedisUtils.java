package pers.project.blog.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static pers.project.blog.constant.GenericConst.ONE_L;
import static pers.project.blog.constant.GenericConst.TRUE_OF_LONG;

/**
 * Redis 工具类
 * <p>
 * 在程序启动初期，内部封装的 RedisTemplate 可能为 null，
 * 可以通过注入 Bean 使用该工具类来避免这一问题。
 * <p>
 * 参见 <a href="http://redisdoc.com/">Redis 命令参考</a>。
 *
 * @author Luo Fei
 * @version 2022/12/25
 */
@Component
@SuppressWarnings("all")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisUtils {

    /**
     * 封装 Redis 操作类
     */
    private static volatile RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }

    // region 自定义

    /**
     * 点赞或者取消赞。
     *
     * @param setKeyPrefix 集合键前缀，与 userInfoId 组成键，每个集合对应于一个用户的点赞数据。
     * @param hashKey      哈希表的键，一个哈希表对应一种内容的点赞数据。
     * @param contentId    点赞内容的 ID，作为集合的元素和哈希表的域名，每个域名对应一个内容的点赞数据。
     */
    public static void likeOrUnlike(@NotNull String setKeyPrefix, @NotNull String hashKey,
                                    @NotNull Integer contentId) {
        // 如果 sAdd 操作成功添加元素，则是点赞操作，否则是取消赞操作
        String setKey = setKeyPrefix + SecurityUtils.getUserInfoId();
        Long like = RedisUtils.sAdd(setKey, contentId);
        if (like.equals(TRUE_OF_LONG)) {
            RedisUtils.hIncrBy(hashKey, contentId.toString(), ONE_L);
            /*
             * 如果要实现内容基于点赞量排行的功能，可以给每种点赞内容关联一个有序集合
             * 有序集合的值为内容的 ID，分数为点赞数量。
             */
        } else {
            RedisUtils.sRem(setKey, contentId);
            RedisUtils.hIncrBy(hashKey, contentId.toString(), -ONE_L);
        }
    }

    // endregion

    // region 通用

    /**
     * 删除给定的一个或多个 key。不存在的 key 会被忽略。
     *
     * @param key 键
     * @return 被删除 key 的数量。
     */
    public static Long del(@NotNull String... key) {
        return redisTemplate.delete(Arrays.asList(key));
    }

    /**
     * 为给定 key 设置生存时间，当 key 过期时（生存时间为 0），它会被自动删除。
     * <p>
     * 生存时间可以通过使用 del 命令来删除整个 key 来移除，或者被 set 和
     * getSet 命令覆写，这意味着，如果一个命令只是修改一个带生存时间的 key
     * 的值而不是用一个新的 key 值来代替它的话，那么生存时间不会被改变。
     * <p>
     * 可以对一个已经带有生存时间的 key 执行 expire 命令，新指定的生存时间
     * 会取代旧的生存时间。
     *
     * @param key     键
     * @param timeout 时间量
     * @param unit    时间单位
     * @return 设置成功返回 true。当 key 不存在或者不能为 key 设置生存时间时，返回 false。
     */
    public static Boolean expire(@NotNull String key, long timeout, @NotNull TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 在管道连接上执行给定的 Redis 会话。允许事务流水线化。
     * <p>
     * <b>请注意，回调 不能 返回非 null 值，因为它会被管道覆盖。</b>
     *
     * @param sessionCallback 会话回调
     * @return 管道返回的对象列表
     */
    @SuppressWarnings("rawtypes")
    public static List executePipelined(SessionCallback sessionCallback) {
        return redisTemplate.executePipelined(sessionCallback);
    }

    // endregion

    // region 字符串

    /**
     * 将字符串值 value 关联到 key。
     * <p>
     * 如果 key 已经持有其他值，SET 就覆写旧值，无视类型。
     * <p>
     * 当 SET 命令对一个带有生存时间（TTL）的键进行设置之后，该键原有的 TTL 将被清除。
     *
     * @param key   键
     * @param value 值
     */
    public static void set(@NotNull String key, @NotNull Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将键 key 的值设置为 value，同时设置键 key 的生存时间。
     * <p>
     * 如果键 key 已经存在，那么 setEx 命令将覆盖已有的值。
     * <p>
     * setEx 的不同之处在于 setEx 是一个原子（atomic）操作，
     * 它可以在同一时间内完成设置值和设置过期时间这两个操作，
     * 因此 setEx 命令在储存缓存的时候非常实用。
     *
     * @param key     键
     * @param value   值
     * @param timeout 时间量
     * @param unit    时间单位
     */
    public static void setEx(@NotNull String key, @NotNull Object value,
                             long timeout, @NotNull TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 将键 key 的值设置为 value，同时设置键 key 的生存时间。
     * <p>
     * 如果键 key 已经存在，那么 setNX 命令没有效果。
     * <p>
     * setNX 是一个原子（atomic）操作，
     *
     * @param key     键
     * @param value   值
     * @param timeout 时间量
     * @param unit    时间单位
     */
    public static void setNX(@NotNull String key, @NotNull Object value,
                             long timeout, @NotNull TimeUnit unit) {
        redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 为键 key 储存的数字值加上一。
     * <p>
     * 如果键 key 不存在，那么它的值会先被初始化为 0，然后再执行 incr 命令。
     *
     * @param key 键
     * @return 返回键 key 在执行加一操作之后的值。
     */
    public static Long incr(@NotNull String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 返回与键 key 相关联的字符串值。
     *
     * @param key 键
     * @return 如果键 key 存在， 那么返回值；否则，返回 null。
     */
    @Nullable
    public static Object get(@NotNull String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // endregion

    // region 哈希表

    /**
     * 返回哈希表给定域的值。
     *
     * @param key   键
     * @param field 域
     * @return 如果键或域不存在，那么命令返回 null。
     */
    @Nullable
    public static Object hGet(@NotNull String key, @NotNull String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 为哈希表 key 中的域 field 的值加上增量 increment。
     * <p>
     * 增量也可以为负数，相当于对给定域进行减法操作。
     * <p>
     * 如果 key 不存在，一个新的哈希表被创建并执行 hIncrBy 命令。
     * <p>
     * 如果 field 不存在，那么在执行命令前，域的值被初始化为 0。
     *
     * @param key       键
     * @param field     域
     * @param increment 增量
     * @return 执行 hIncrBy 命令之后，哈希表 key 中域 field 的值。
     */
    @NotNull
    public static Long hIncrBy(@NotNull String key, @NotNull String field, long increment) {
        return redisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * 返回哈希表 key 中，所有的域和值。
     *
     * @param key 键
     * @return 以映射形式返回哈希表的域和域的值。
     */
    @NotNull
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map<String, Object> hGetAll(@NotNull String key) {
        return (Map) redisTemplate.opsForHash().entries(key);
    }

    // endregion

    // region 集合

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合
     * 的 member 元素将被忽略。
     * <p>
     * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
     *
     * @param key    键
     * @param member 成员
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
     */
    public static Long sAdd(@NotNull String key, Object... member) {
        return redisTemplate.opsForSet().add(key, member);
    }

    /**
     * 判断 member 元素是否是集合 key 的成员。
     *
     * @param key    键
     * @param member 可能的成员
     * @return 如果 member 元素是集合的成员，返回 true。
     * 如果 member 元素不是集合的成员，或 key 不存在，返回 false。
     */
    public static Boolean sIsMember(@NotNull String key, Object member) {
        return redisTemplate.opsForSet().isMember(key, member);
    }

    /**
     * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
     *
     * @param key    键
     * @param member 一个或多个 member 元素
     * @return 被成功移除的元素的数量，不包括被忽略的元素。
     */
    public static Long sRem(@NotNull String key, Object... member) {
        return redisTemplate.opsForSet().remove(key, member);
    }

    /**
     * 返回集合 key 中元素的数量。
     *
     * @param key 键
     * @return 集合中元素的数量。当 key 不存在时，返回 0。
     */
    public static Long sCard(@NotNull String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 返回集合 key 中的所有成员。
     * <p>
     * 不存在的 key 被视为空集合。
     *
     * @param key 键
     * @return 集合中的所有成员。
     */
    public static Set<Object> sMembers(@NotNull String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // endregion

    // region 有序集合

    /**
     * 返回有序集 key 中，成员 member 的 score 值。
     * <p>
     * 如果 member 元素不是有序集 key 的成员，或 key 不存在，返回 null 。
     *
     * @param key    键
     * @param member 成员
     * @return 成员 member 的 score 值。
     */
    @Nullable
    public static Double zScore(@NotNull String key, Object member) {
        return redisTemplate.opsForZSet().score(key, member);
    }

    /**
     * 为有序集 key 的成员 member 的 score 值加上增量 increment 。
     * <p>
     * 可以通过传递一个负数值 increment ，让 score 减去相应的值。
     * <p>
     * 当 key 不存在，或 member 不是 key 的成员时，则进行新建。
     *
     * @param key       键
     * @param member    成员
     * @param increment 增量
     * @return member 成员的新 score 值。
     */
    public static Double zIncyBy(String key, Object member, double increment) {
        return redisTemplate.opsForZSet().incrementScore(key, member, increment);
    }

    /**
     * 返回有序集 key 中，指定区间内的成员和分数的映射。
     * <p>
     * 其中成员的位置按 score 值递增（从小到大）来排序。
     * <p>
     * 具有相同 score 值的成员按字典序来排列。
     * <p>
     * 下标参数 start 和 end 都以 0 为底，也就是说，以 0 表示有序集第一个成员，
     * 以 1 表示有序集第二个成员，以此类推。你也可以使用负数下标，以 -1 表示最后
     * 一个成员， -2 表示倒数第二个成员，以此类推。
     * <p>
     * 超出范围的下标并不会引起错误。比如说，当 start 的值比有序集的最大下标还要
     * 大，或是 start > stop 时，zRange 命令只是简单地返回一个空列表。 另一方
     * 面，假如 end 参数的值比有序集的最大下标还要大，那么 Redis 将 end 当作
     * 最大下标来处理。
     *
     * @param key   键，不能为空
     * @param start 起始下标
     * @param end   终止下标
     * @return 成员和分数的映射，按分数升序排序。
     */
    @NotNull
    @SuppressWarnings("all")
    public static Map<Object, Double> zRangeWithScores(@NotNull String key, int start, int end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end)
                .stream()
                .collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue,
                        ZSetOperations.TypedTuple::getScore,
                        Double::sum, LinkedHashMap::new));
    }

    /**
     * 返回有序集 key 中，指定区间内的成员和分数的映射。
     * <p>
     * 除了成员按 score 值递减的次序排列这一点外，该命令的其他方面
     * 和 {@link RedisUtils#zRangeWithScores(String, int, int)} 命令一样。
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return 成员和分数的映射，按分数降序排序。
     */
    @NotNull
    @SuppressWarnings("all")
    public static Map<Object, Double> zRevRangeWithScores(@NotNull String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end)
                .stream()
                .collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue,
                        ZSetOperations.TypedTuple::getScore,
                        Double::sum, LinkedHashMap::new));
    }

    // endregion

    // region HyperLogLog

    /**
     * 将任意数量的元素添加到指定的 HyperLogLog 里面。
     *
     * @param key    键
     * @param values 任意数量的元素
     * @return 如果 HyperLogLog 的内部储存被修改了，那么返回 1，否则返回 0。
     */
    @NotNull
    public static Long pfAdd(@NotNull String key, @NotNull Object... values) {
        return redisTemplate.opsForHyperLogLog().add(key, values);
    }

    /**
     * 当 pfCount 命令作用于单个键时，返回储存在给定键的 HyperLogLog 的近似基数，
     * 如果键不存在，那么返回 0。
     * <p>
     * 当 pfCount 命令作用于多个键时，返回所有给定 HyperLogLog 的并集的近似基数，
     * 这个近似基数是通过将所有给定 HyperLogLog 合并至一个临时 HyperLogLog 来计算得出的。
     *
     * @param keys 单个或多个键
     * @return 给定 HyperLogLog 包含的唯一元素的近似数量。
     */
    @NotNull
    public static Long pfCount(@NotNull String... keys) {
        return redisTemplate.opsForHyperLogLog().size(keys);
    }

    /**
     * 将多个 HyperLogLog 合并为一个 HyperLogLog，合并后的 HyperLogLog 的基数
     * 接近于所有输入 HyperLogLog 的可见集合的并集。
     * <p>
     * 合并得出的 HyperLogLog 会被储存在 destination 键里面，如果该键并不存在，
     * 那么命令在执行之前，会先为该键创建一个空的 HyperLogLog。
     *
     * @param destination 用于合并的 HyperLogLog 键
     * @param sourceKeys  一个或多个要合并的 HyperLogLog 键
     * @return 合并得出的 HyperLogLog 包含的唯一元素的近似数量。
     */
    @NotNull
    public static Long pfMerge(@NotNull String destination, @NotNull String... sourceKeys) {
        return redisTemplate.opsForHyperLogLog().union(destination, sourceKeys);
    }

    // endregion

}
