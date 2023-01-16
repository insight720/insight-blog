package pers.project.blog.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {

    /**
     * 单位时间（秒）
     *
     * @return int
     */
    int seconds();

    /**
     * 单位时间最大请求次数
     *
     * @return int
     */
    int maxCount();

}
