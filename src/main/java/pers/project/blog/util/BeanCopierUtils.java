package pers.project.blog.util;


import lombok.Data;
import org.springframework.cglib.beans.BeanCopier;

import java.util.concurrent.ConcurrentHashMap;

/**
 * BeanCopier 工具类
 *
 * @author Luo Fei
 * @date 2023/3/15
 */
public abstract class BeanCopierUtils {

    private static final ConcurrentHashMap<BeanCopierId, BeanCopier>
            BEAN_COPIER_CACHE_MAP = new ConcurrentHashMap<>();

    @Data
    private static class BeanCopierId {
        private final Class<?> source;
        private final Class<?> target;
    }

    /**
     * 将给定源 Bean 的属性值复制到目标 Bean 中。
     * <p>
     * 注意：属性类型和名称必须匹配，并且源 Bean 属性要有 Getter 方法，
     * 目标 Bean 属性要有 Setter 方法。源 Bean 公开但目标 Bean
     * 未公开的任何 Bean 属性都将被静默忽略。
     *
     * @param source 源 Bean
     * @param target 目标 Bean
     */
    public static void copyProperties(Object source, Object target) {
        BeanCopierId copierId = new BeanCopierId(source.getClass(), target.getClass());
        // computeIfAbsent 以原子方式执行
        BeanCopier beanCopier = BEAN_COPIER_CACHE_MAP.computeIfAbsent
                (copierId, id -> BeanCopier.create(id.source, id.target, false));
        beanCopier.copy(source, target, null);
    }

    /**
     * 将给定源对象的属性值复制到目标类对象中。
     * <p>
     * 注意：目标类必须有可访问的空参构造函数。
     *
     * @param source 源对象
     * @param tClass 目标类的 Class 对象
     * @return 目标类对象
     * @see BeanCopierUtils#copyProperties(Object, Object)
     */
    public static <T> T copy(Object source, Class<T> tClass) {
        T target;
        try {
            target = tClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (source != null) {
            BeanCopierUtils.copyProperties(source, target);
        }
        return target;
    }

}


