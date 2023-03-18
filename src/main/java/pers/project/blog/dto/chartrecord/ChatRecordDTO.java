package pers.project.blog.dto.chartrecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.blog.entity.ChatRecord;

import java.util.List;

/**
 * 聊天记录数据
 *
 * @author Luo Fei
 * @version 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRecordDTO {

    /**
     * 聊天记录
     */
    private List<ChatRecord> chatRecordList;

    /**
     * IP 地址
     */
    private String ipAddress;

    /**
     * IP来源
     */
    private String ipSource;

}
