package pers.project.blog.constant.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 说说状态枚举
 *
 * @author Luo Fei
 * @date 2023/1/11
 */
@Getter
@AllArgsConstructor
public enum TalkStatusEnum {

    /**
     * 公开
     */
    PUBLIC(1, "公开"),

    /**
     * 私密
     */
    SECRET(2, "私密");

    /**
     * 状态
     */
    private final Integer status;

    /**
     * 描述
     */
    private final String description;

}
