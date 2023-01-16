package pers.project.blog.constant.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;
import pers.project.blog.dto.Result;

/**
 * {@link Result} 返回状态枚举
 *
 * @author Luo Fei
 * @date 2022/12/23
 */
@Getter
@AllArgsConstructor
public enum ResultStatusEnum {

    /**
     * 操作成功
     */
    SUCCESS(20000, "操作成功"),

    /**
     * 操作失败
     */
    FAILURE(30000, "操作失败"),

    /**
     * 认证失败
     */
    AUTHENTICATION_FAILURE(51000, "用户名或密码错误"),

    /**
     * 没有认证
     */
    AUTHENTICATION_REQUIRED(40001, "用户未登录"),

    /**
     * 没有授权
     */
    ACCESS_DENIED(40300, "没有权限操作"),

    /**
     * qq登录错误
     */
    QQ_LOGIN_ERROR(53001, "qq登录错误"),

    /**
     * 微博登录错误
     */
    WEIBO_LOGIN_ERROR(53002, "微博登录错误");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String description;

}
