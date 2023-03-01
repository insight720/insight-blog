package pers.project.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录方式枚举
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    /**
     * 邮箱登录
     */
    EMAIL(1, "邮箱登录", null),

    /**
     * QQ 登录
     */
    QQ(2, "QQ登录", "QQLoginStrategyImpl"),

    /**
     * 微博登录
     */
    WEIBO(3, "微博登录", "weiboLoginStrategyImpl");

    /**
     * 登录方式
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 策略
     */
    private final String strategy;

}
