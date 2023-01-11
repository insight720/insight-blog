package pers.project.blog.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author Luo Fei
 * @date 2023/1/1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作类型
     */
    String type();

}
