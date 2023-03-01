package pers.project.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    /**
     * 管理员
     */
    ADMIN(1, "管理员", "admin"),

    /**
     * 普通用户
     */
    USER(2, "用户", "user"),

    /**
     * 测试账号
     */
    TEST(3, "测试", "test");

    /**
     * 角色 ID
     */
    private final Integer roleId;

    /**
     * 描述
     */
    private final String name;

    /**
     * 权限标签
     */
    private final String label;

}
