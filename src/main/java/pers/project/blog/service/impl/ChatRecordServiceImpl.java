package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.constant.FilePathConst;
import pers.project.blog.dto.chartrecord.ChatRecordDTO;
import pers.project.blog.dto.chartrecord.WebSocketMessageDTO;
import pers.project.blog.entity.ChatRecord;
import pers.project.blog.enums.ChatTypeEnum;
import pers.project.blog.handler.WebSocketHandler;
import pers.project.blog.mapper.ChatRecordMapper;
import pers.project.blog.service.ChatRecordService;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.util.BeanCopierUtils;
import pers.project.blog.util.TimeUtils;
import pers.project.blog.util.WebUtils;
import pers.project.blog.vo.bloginfo.VoiceVO;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static pers.project.blog.constant.GenericConst.TWELVE;

/**
 * 针对表【tb_chat_record】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-13
 */
@Service
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecord> implements ChatRecordService {

    @Override
    public ChatRecordDTO getChatRecord(String ipAddress) {
        // 获取 12 小时内的聊天记录
        List<ChatRecord> chatRecordList = lambdaQuery()
                .ge(ChatRecord::getCreateTime,
                        TimeUtils.offset(TimeUtils.now(), -TWELVE, ChronoUnit.HOURS))
                .list();
        return ChatRecordDTO.builder()
                .chatRecordList(chatRecordList)
                .ipAddress(ipAddress)
                .ipSource(WebUtils.getIpSource(ipAddress))
                .build();
    }

    /**
     * 异步更新在线人数
     */
    @Async
    @Override
    public void updateOnlineCount(Integer size) {
        // 获取当前在线人数
        WebSocketMessageDTO messageDTO = WebSocketMessageDTO.builder()
                .type(ChatTypeEnum.ONLINE_COUNT.getType())
                .data(size)
                .build();
        // 广播消息
        WebSocketHandler.broadcastMessage(messageDTO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String sendVoice(VoiceVO voiceVO) {
        // 上传音频文件
        String contentUrl = UploadContext.executeStrategy(voiceVO.getFile(), FilePathConst.VOICE_DIR);
        voiceVO.setContent(contentUrl);
        // 保存记录
        ChatRecord chatRecord = BeanCopierUtils.copy(voiceVO, ChatRecord.class);
        save(chatRecord);
        // 发送消息
        WebSocketMessageDTO messageDTO = WebSocketMessageDTO.builder()
                .type(ChatTypeEnum.VOICE_MESSAGE.getType())
                .data(chatRecord)
                .build();
        WebSocketHandler.broadcastMessage(messageDTO);
        return contentUrl;
    }

}




