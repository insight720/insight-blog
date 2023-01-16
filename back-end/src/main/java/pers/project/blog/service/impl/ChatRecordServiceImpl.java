package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pers.project.blog.constant.DirectoryUriConstant;
import pers.project.blog.constant.enumeration.ChatTypeEnum;
import pers.project.blog.dto.ChatRecordDTO;
import pers.project.blog.dto.WebSocketMessageDTO;
import pers.project.blog.entity.ChatRecordEntity;
import pers.project.blog.handler.WebSocketHandler;
import pers.project.blog.mapper.ChatRecordMapper;
import pers.project.blog.service.ChatRecordService;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.IpUtils;
import pers.project.blog.util.TimeUtils;
import pers.project.blog.vo.VoiceVO;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 针对表【tb_chat_record】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-13
 */
@Service
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecordEntity> implements ChatRecordService {

    @Override
    public ChatRecordDTO getChatRecord(String ipAddress) {
        // 获取 12 小时内的聊天记录
        List<ChatRecordEntity> chatRecordList = lambdaQuery()
                .ge(ChatRecordEntity::getCreateTime,
                        TimeUtils.offset(TimeUtils.now(), -12L, ChronoUnit.HOURS))
                .list();

        return ChatRecordDTO.builder()
                .chatRecordList(chatRecordList)
                .ipAddress(ipAddress)
                .ipSource(IpUtils.getIpSource(ipAddress))
                .build();
    }

    /**
     * 更新在线人数
     *
     * @throws IOException IO 异常
     */
    @Async
    @Override
    public void UpdateOnlineCount(Integer size) throws IOException {
        // TODO: 2023/1/13 封装异步工具类，异步的意义不明
        // 获取当前在线人数
        WebSocketMessageDTO messageDTO = WebSocketMessageDTO.builder()
                .type(ChatTypeEnum.ONLINE_COUNT.getType())
                .data(size)
                .build();

        // 广播消息
        WebSocketHandler.broadcastMessage(messageDTO);
    }

    @Override
    public void sendVoice(VoiceVO voiceVO) throws IOException {
        // 上传音频文件
        UploadContext.executeStrategy(voiceVO.getFile(), DirectoryUriConstant.VOICE);

        // 保存记录
        ChatRecordEntity chatRecordEntity
                = ConversionUtils.convertObject(voiceVO, ChatRecordEntity.class);
        save(chatRecordEntity);

        // 发送消息
        WebSocketMessageDTO messageDTO = WebSocketMessageDTO.builder()
                .type(ChatTypeEnum.VOICE_MESSAGE.getType())
                .data(chatRecordEntity)
                .build();
        WebSocketHandler.broadcastMessage(messageDTO);
    }

}




