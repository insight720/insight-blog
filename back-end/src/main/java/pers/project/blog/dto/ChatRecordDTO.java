package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.blog.entity.ChatRecordEntity;

import java.util.List;

/**
 * 聊天记录数据
 *
 * @author Luo Fei
 * @date 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRecordDTO {

    /**
     * 聊天记录
     */
    private List<ChatRecordEntity> chatRecordList;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * IP来源
     */
    private String ipSource;

}
