package pers.project.blog.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * 结果枚举
 *
 * @author Luo Fei
 * @date 2022/12/23
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {

    /**
     * 操作成功
     */
    SUCCESS(TRUE, 10000, "操作成功"),

    /**
     * 操作失败
     */
    FAILURE(FALSE, 20000, "操作失败"),

    /**
     * 认证失败
     */
    AUTHENTICATION_FAILURE(FALSE, 30000, "用户名或密码错误"),

    /**
     * 认证禁用
     */
    AUTHENTICATION_DISABLED(FALSE, 40000, "用户已被禁用"),

    /**
     * 认证锁定
     */
    AUTHENTICATION_LOCKED(FALSE, 50000, "用户无可用权限角色"),

    /**
     * 没有认证
     */
    AUTHENTICATION_REQUIRED(FALSE, 60000, "用户未登录"),

    /**
     * 没有授权
     */
    ACCESS_DENIED(FALSE, 70000, "没有权限操作"),

    /**
     * 运行异常
     */
    INTERNAL_SERVER_ERROR(FALSE, 80000, "服务器内部错误"),

    /**
     * 请求过于频繁
     */
    REQUEST_BLOCKED(FALSE, 80000, "请求过于频繁，请稍后再试试");

    /**
     * 状态标识
     */
    private final Boolean flag;

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 信息
     */
    private final String message;

}
