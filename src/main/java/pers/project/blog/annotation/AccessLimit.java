package pers.project.blog.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 *
 * @author Luo Fei
 * @version 2023/1/14
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {

    /**
     * 单位时间（秒）
     */
    int seconds();

    /**
     * 单位时间最大请求次数
     */
    int maxCount();

}
