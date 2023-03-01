package pers.project.blog.annotation;

import pers.project.blog.enums.OperationLogEnum;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author Luo Fei
 * @date 2023/1/1
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperatingLog {

    /**
     * 操作类型
     */
    OperationLogEnum type();

}
