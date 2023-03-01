package pers.project.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户类型枚举
 *
 * @author Luo Fei
 * @date 2022/12/29
 */
@Getter
@AllArgsConstructor
public enum UserTypeEnum {

    /**
     * 用户
     */
    USER(1, "用户"),

    /**
     * 游客
     */
    VISITOR(2, "游客");

    /**
     * 用户区域类型与其枚举的映射
     */
    private static final Map<Integer, UserTypeEnum> AREA_TYPE_ENUM_MAP;

    static {
        AREA_TYPE_ENUM_MAP = Arrays.stream(UserTypeEnum.values())
                .collect(Collectors.toMap(UserTypeEnum::getType,
                        Function.identity(),
                        (firstEnum, secondEnum) -> firstEnum,
                        () -> CollectionUtils.newHashMap(UserTypeEnum.values().length)));
    }

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String description;

    /**
     * 获取用户类型枚举
     *
     * @param type 类型
     * @return 用户类型枚举
     */
    public static UserTypeEnum get(Integer type) {
        UserTypeEnum typeEnum = AREA_TYPE_ENUM_MAP.get(type);
        if (typeEnum == null) {
            throw new RuntimeException("用户类型不存在");
        }
        return typeEnum;
    }

}
