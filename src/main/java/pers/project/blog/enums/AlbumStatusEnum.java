package pers.project.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 相册状态枚举
 *
 * @author Luo Fei
 * @version 2023/1/11
 */
@Getter
@AllArgsConstructor
public enum AlbumStatusEnum {

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
