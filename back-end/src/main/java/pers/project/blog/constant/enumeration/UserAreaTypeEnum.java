package pers.project.blog.constant.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户区域类型枚举
 *
 * @author Luo Fei
 * @date 2022/12/29
 */
@Getter
@AllArgsConstructor
public enum UserAreaTypeEnum {

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
    private static final Map<Integer, UserAreaTypeEnum> AREA_TYPE_ENUM_MAP;

    static {
        AREA_TYPE_ENUM_MAP = Arrays.stream(UserAreaTypeEnum.values())
                .collect(Collectors.toMap(UserAreaTypeEnum::getType,
                        Function.identity(),
                        (firstEnum, secondEnum) -> firstEnum,
                        () -> CollectionUtils.newHashMap(UserAreaTypeEnum.values().length)));
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
     * 获取用户区域类型枚举
     *
     * @param type 类型
     * @return 用户区域类型枚举
     */
    @Nullable
    public static UserAreaTypeEnum get(Integer type) {
        return AREA_TYPE_ENUM_MAP.get(type);
    }

}
