package pers.project.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文章状态枚举
 *
 * @author Luo Fei
 * @date 2023/1/7
 */
@Getter
@AllArgsConstructor
public enum ArticelStateEnum {

    /**
     * 公开
     */
    PUBLIC(1, "公开"),

    /**
     * 私密
     */
    SECRET(2, "私密"),

    /**
     * 草稿
     */
    DRAFT(3, "草稿");

    /**
     * 状态
     */
    private final Integer status;

    /**
     * 描述
     */
    private final String description;

}
