package pers.project.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 聊天类型枚举
 *
 * @author Luo Fei
 * @version 2023/1/13
 */
@Getter
@AllArgsConstructor
public enum ChatTypeEnum {

    /**
     * 在线人数
     */
    ONLINE_COUNT(1, "在线人数"),

    /**
     * 历史记录
     */
    HISTORY_RECORD(2, "历史记录"),

    /**
     * 发送消息
     */
    SEND_MESSAGE(3, "发送消息"),

    /**
     * 撤回消息
     */
    RECALL_MESSAGE(4, "撤回消息"),

    /**
     * 语音消息
     */
    VOICE_MESSAGE(5, "语音消息"),

    /**
     * 心跳消息
     */
    HEART_BEAT(6, "心跳消息");

    /**
     * 聊天类型与其枚举的映射
     */
    private static final Map<Integer, ChatTypeEnum> CHAT_TYPE_ENUM_MAP;

    static {
        CHAT_TYPE_ENUM_MAP = Arrays.stream(ChatTypeEnum.values())
                .collect(Collectors.toMap(ChatTypeEnum::getType,
                        Function.identity(),
                        (firstEnum, secondEnum) -> firstEnum,
                        () -> CollectionUtils.newHashMap(ChatTypeEnum.values().length)));
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
     * 获取聊天类型枚举
     *
     * @param type 类型
     * @return {@code ChatTypeEnum} 聊天类型枚举
     */
    public static ChatTypeEnum get(Integer type) {
        ChatTypeEnum typeEnum = CHAT_TYPE_ENUM_MAP.get(type);
        if (typeEnum == null) {
            throw new RuntimeException("聊天类型不存在");
        }
        return typeEnum;
    }

}
