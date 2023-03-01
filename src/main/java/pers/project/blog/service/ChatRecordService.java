package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.chartrecord.ChatRecordDTO;
import pers.project.blog.entity.ChatRecord;
import pers.project.blog.vo.bloginfo.VoiceVO;

/**
 * 针对表【tb_chat_record】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-13
 */
public interface ChatRecordService extends IService<ChatRecord> {

    /**
     * 获取十二小时内聊天记录数据
     */
    ChatRecordDTO getChatRecord(String ipAddress);

    /**
     * 异步更新在线人数
     */
    void updateOnlineCount(Integer count);

    /**
     * 发送音频
     *
     * @return {@link  String} 音频 URL
     */
    String sendVoice(VoiceVO voiceVO);

}
