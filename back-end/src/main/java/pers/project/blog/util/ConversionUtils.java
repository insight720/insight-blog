package pers.project.blog.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类型转换的工具类
 *
 * @author Luo Fei
 * @date 2022/12/23
 */
@Slf4j
public abstract class ConversionUtils {

    /**
     * 将给定源对象的字段值复制到目标类对象中。
     *
     * <p>
     * <b>注意：目标类必须有可访问的空参构造函数。只要属性匹配，源类和目标类不必相互匹配，甚至不必相互派生。
     * 源类公开但目标类不公开的任何字段将被静默忽略。</b>
     *
     * @param source      源对象
     * @param targetClass 目标类的 Class，不能为 null
     * @return T 目标类对象
     */
    public static <T> T convertObject(Object source, @NotNull Class<T> targetClass) {
        T t = null;
        try {
            t = targetClass.newInstance();
            if (source != null) {
                BeanUtils.copyProperties(source, t);
            }
        } catch (Exception e) {
            log.error("转换异常", e);
        }
        return t;
    }

    /**
     * 用给定源列表生成目标类的列表。
     *
     * <p>
     * <b>注意：目标类必须有可访问的空参构造函数。只要属性匹配，源列表元素和目标类不必相互匹配，甚至不必相互派生。
     * 源列表元素公开但目标类不公开的任何字段将被静默忽略。</b>
     *
     * @param sourceList  源列表
     * @param targetClass 目标类的 Class，不能为 null
     * @return T 目标类对象
     */
    public static <T> List<T> covertList(List<?> sourceList, @NotNull Class<T> targetClass) {
        if (sourceList == null) {
            return new ArrayList<>();
        }

        return sourceList.stream()
                .map(sourceElement -> ConversionUtils.convertObject(sourceElement, targetClass))
                .collect(Collectors.toList());
    }

    /**
     * 使用基于字段的方式将对象序列化为 JSON 字符串
     *
     * @param object 要序列化为 JSON 的对象
     * @return 对象的 JSON 字符串
     */
    public static String getJson(Object object) {
        return JSON.toJSONString(object, JSONWriter.Feature.FieldBased);
    }

    /**
     * 使用基于字段的方式将 JSON 字符串解析为对象
     *
     * @param jsonString 要解析的 JSON 字符串
     * @return 解析出的对象
     */
    public static <T> T parseJson(String jsonString, Class<T> tClass) {
        return JSON.parseObject(jsonString, tClass, JSONReader.Feature.FieldBased);
    }

}
