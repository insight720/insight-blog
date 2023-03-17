package pers.project.blog.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 转换工具类
 *
 * @author Luo Fei
 * @date 2022/12/23
 */
public abstract class ConvertUtils {

    /**
     * 将给定源对象的字段值复制到目标类对象中
     *
     * <p>
     * <b>注意：目标类必须有可访问的空参构造函数。只要属性匹配，源类和目标类不必相互匹配，甚至不必相互派生。
     * 源类公开但目标类不公开的任何字段将被静默忽略。</b>
     *
     * @param source 源对象
     * @param tClass 目标类的 Class
     * @return 目标类对象
     * @deprecated 推荐使用 {@link  BeanCopierUtils#copy(Object, Class)}，它效率更高。
     */
    @NotNull
    @Deprecated
    public static <T> T convert(@Nullable Object source, @NotNull Class<T> tClass) {
        T t;
        try {
            t = tClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("对象转换异常", e);
        }
        if (source != null) {
            BeanUtils.copyProperties(source, t);
        }
        return t;
    }

    /**
     * 用给定源集合生成目标类的列表
     * <p>
     *
     * @param collection 源集合
     * @param tClass     目标类的 Class
     * @return 目标类的列表
     * @see BeanCopierUtils#copy(Object, Class)
     */
    @NotNull
    public static <T> List<T> convertList(@Nullable Collection<?> collection, @NotNull Class<T> tClass) {
        return reCollect(collection, ArrayList::new, tClass);
    }

    /**
     * 用给定源集合生成目标类的目标集合
     *
     * @param collection 源集合
     * @param cSupplier  目标集合提供者
     * @param tClass     目标类的 Class
     * @return 目标类的目标集合
     * @see BeanCopierUtils#copy(Object, Class)
     */
    @NotNull
    public static <T, C extends Collection<T>> C reCollect
    (@Nullable Collection<?> collection, @NotNull Supplier<C> cSupplier, @NotNull Class<T> tClass) {
        if (collection == null) {
            return cSupplier.get();
        }
        return collection
                .stream()
                .map(element -> BeanCopierUtils.copy(element, tClass))
                .collect(Collectors.toCollection(cSupplier));
    }

    /**
     * 将类对象的引用列表转换为目标类或接口的引用列表
     * <p>
     * 用于判空和限制编译器警告。
     *
     * @param list 类对象的引用列表
     * @return 目标类或接口的引用列表
     */
    @NotNull
    public static <T> List<T> castList(@Nullable Object list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return cast(list);
    }

    /**
     * 将类对象的引用 Set 集合转换为目标类或接口的引用 Set 集合
     * <p>
     * 用于判空和限制编译器警告。
     *
     * @param set 类对象的引用集合
     * @return 目标类或接口的引用 Set 集合
     */
    @NotNull
    public static <T> Set<T> castSet(@Nullable Object set) {
        if (set == null) {
            return new HashSet<>();
        }
        return cast(set);
    }

    /**
     * 将集合的引用转换为目标集合的引用
     * 用户限制编译器警告。
     *
     * @param collection 集合引用
     * @return 目标集合引用
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <C extends Collection<T>, T> C cast(@NotNull Object collection) {
        return ((C) collection);
    }


    /**
     * 使用基于字段的方式将对象序列化为 JSON 字符串
     *
     * @param object 要序列化为 JSON 的对象
     * @return 对象的 JSON 字符串
     */
    @NotNull
    public static String getJson(@NotNull Object object) {
        return JSON.toJSONString(object, JSONWriter.Feature.FieldBased);
    }

    /**
     * 使用基于字段的方式将对象序列化为 JSON 字符数组
     *
     * @param object 要序列化为 JSON 字符数组的对象
     * @return 对象的 JSON 符数组
     */
    @NotNull
    public static byte[] getJsonBytes(@NotNull Object object) {
        return JSON.toJSONBytes(object, JSONWriter.Feature.FieldBased);
    }

    /**
     * 使用基于字段的方式将 JSON 字符串解析为对象
     *
     * @param jsonString 要解析的 JSON 字符串，
     *                   用 {@link  StrRegexUtils#isBlank(CharSequence)}}
     *                   <p>
     *                   检查不能返回 null。
     * @param tClass     对象的 Class
     * @return 解析出的对象
     */
    @NotNull
    public static <T> T parseJson(@NotNull String jsonString, @NotNull Class<T> tClass) {
        return JSON.parseObject(jsonString, tClass, JSONReader.Feature.FieldBased);
    }

    /**
     * 使用基于字段的方式将 JSON 字符数组解析为对象
     *
     * @param bytes  要解析的 JSON 字符数组
     * @param tClass 对象的 Class
     * @return 解析出的对象
     */
    @NotNull
    public static <T> T parseJsonBytes(@NotNull byte[] bytes, @NotNull Class<T> tClass) {
        return JSON.parseObject(bytes, tClass, JSONReader.Feature.FieldBased);
    }

}
