package pers.project.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论类型枚举
 *
 * @author Luo Fei
 * @version 2023/1/15
 */
@Getter
@AllArgsConstructor
public enum CommentTypeEnum {

    /**
     * 文章评论
     */
    ARTICLE(1, "文章评论", "/articles/"),

    /**
     * 友链评论
     */
    LINK(2, "友链评论", "/links/"),

    /**
     * 说说评论
     */
    TALK(3, "说说评论", "/talks/");

    /**
     * 状态
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 路径
     */
    private final String path;

    /**
     * 获取评论路径
     *
     * @param type 类型
     * @return {@link String}
     */
    public static String getCommentPath(Integer type) {
        for (CommentTypeEnum value : CommentTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value.getPath();
            }
        }
        throw new RuntimeException("评论路径不存在");
    }

    /**
     * 获取评论枚举
     *
     * @param type 类型
     * @return {@link CommentTypeEnum}
     */
    public static CommentTypeEnum get(Integer type) {
        for (CommentTypeEnum value : CommentTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new RuntimeException("评论类型不存在");
    }

}
