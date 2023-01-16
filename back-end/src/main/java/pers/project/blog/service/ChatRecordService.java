package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.ChatRecordDTO;
import pers.project.blog.entity.ChatRecordEntity;
import pers.project.blog.vo.VoiceVO;

import java.io.IOException;

/**
 * 针对表【tb_chat_record】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-13
 */
public interface ChatRecordService extends IService<ChatRecordEntity> {

    /**
     * 获取聊天记录数据
     *
     * @param ipAddress IP 地址
     * @return {@code ChatRecordDTO} 聊天记录数据
     */
    ChatRecordDTO getChatRecord(String ipAddress);

    /**
     * 更新在线人数
     *
     * @param count 在线人数
     * @throws IOException IO 异常
     */
    void UpdateOnlineCount(Integer count) throws IOException;

    /**
     * 发送音频
     *
     * @param voiceVO 音频数据
     */
    void sendVoice(VoiceVO voiceVO) throws IOException;

}
