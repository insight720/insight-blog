package pers.project.blog.handler;




import cn.hutool.core.lang.intern.InternUtil;
import cn.hutool.core.lang.intern.Interner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.chartrecord.ChatRecordDTO;
import pers.project.blog.dto.chartrecord.RecalledMessageDTO;
import pers.project.blog.dto.chartrecord.WebSocketMessageDTO;
import pers.project.blog.entity.ChatRecord;
import pers.project.blog.enums.ChatTypeEnum;
import pers.project.blog.service.ChatRecordService;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.util.StrRegexUtils;
import pers.project.blog.util.WebUtils;

import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static pers.project.blog.constant.WebSocketConst.PONG;
import static pers.project.blog.constant.WebSocketConst.X_FORWARDED_FOR;


/**
 * WebSocket 处理程序
 *
 * @author Luo Fei
 * @version 2023/1/12
 */
@Slf4j
@Component
@SuppressWarnings("unused")
@ServerEndpoint(value = "/websocket", configurator = WebSocketHandler.ChatConfigurator.class)
public class WebSocketHandler {

    /**
     * {@link ConcurrentHashMap} 支持的线程安全 Set，用来存放每个客户端对应的 WebSocketHandler 对象
     */
    private static final Set<WebSocketHandler>
            WEB_SOCKET_HANDLER_SET = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * 弱引用线程安全中间器。当 JVM 进行垃圾回收时，无论内存是否充足，都会回收被弱引用关联的对象。
     */
    public static final Interner<String> weakInterner = InternUtil.createWeakInterner();

    /**
     * 聊天记录服务
     */
    private static ChatRecordService chatRecordService;

    @Autowired
    public void setChatRecordService(ChatRecordService chatRecordService) {
        WebSocketHandler.chatRecordService = chatRecordService;
    }

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 获取客户端 IP
     */

    public static class ChatConfigurator extends ServerEndpointConfig.Configurator {

        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            String currentIpAddress = WebUtils.getCurrentIpAddress();
            if (log.isDebugEnabled()) {
                // 用于测试是否能正确获取 IP
                log.debug("WebSocket 握手，HandshakeRequest 请求头: {}", request.getHeaders());
                log.debug("WebSocket 握手，客户端 IP: {}", currentIpAddress);
            }
            sec.getUserProperties().put(X_FORWARDED_FOR, currentIpAddress);
        }

    }

    /**
     * 连接建立成功调用的方法
     *
     * @param session        与某个客户端的连接会话
     * @param endpointConfig 服务端点配置
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // 加入连接
        this.session = session;
        WEB_SOCKET_HANDLER_SET.add(this);
        // 异步更新在线人数
        chatRecordService.updateOnlineCount(WEB_SOCKET_HANDLER_SET.size());
        // 加载聊天记录
        String ipAddress = endpointConfig
                .getUserProperties().get(X_FORWARDED_FOR).toString();
        ChatRecordDTO chatRecordDTO = chatRecordService.getChatRecord(ipAddress);
        // 发送消息
        WebSocketMessageDTO messageDTO = WebSocketMessageDTO.builder()
                .type(ChatTypeEnum.HISTORY_RECORD.getType())
                .data(chatRecordDTO)
                .build();
        sendMessage(session, messageDTO, "发送历史聊天记录异常");
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        WebSocketMessageDTO messageDTO = ConvertUtils.parseJson
                (message, WebSocketMessageDTO.class);
        ChatTypeEnum chatTypeEnum = ChatTypeEnum.get(messageDTO.getType());
        switch (chatTypeEnum) {
            case SEND_MESSAGE:
                // 发送的消息
                ChatRecord chatRecord = ConvertUtils.parseJson
                        (ConvertUtils.getJson(messageDTO.getData()), ChatRecord.class);
                // 过滤消息内容
                chatRecord.setContent(StrRegexUtils.filter(chatRecord.getContent()));
                chatRecordService.save(chatRecord);
                // 广播消息
                broadcastMessage(messageDTO);
                break;
            case RECALL_MESSAGE:
                // 撤回的消息
                RecalledMessageDTO recalledMessageDTO = ConvertUtils.parseJson
                        (ConvertUtils.getJson(messageDTO.getData()), RecalledMessageDTO.class);
                // 删除消息记录
                chatRecordService.removeById(recalledMessageDTO.getId());
                // 广播消息
                broadcastMessage(messageDTO);
                break;
            case HEART_BEAT:
                // 心跳消息
                messageDTO.setData(PONG);
                sendMessage(session, messageDTO, "心跳消息异常");
                break;
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        // 移除用户会话
        WEB_SOCKET_HANDLER_SET.remove(this);
        // 异步更新在线人数
        chatRecordService.updateOnlineCount(WEB_SOCKET_HANDLER_SET.size());
    }

    /**
     * 连接错误时调用的方法
     */
    @OnError
    public void onError(Session session, Throwable cause) {
        onClose();
        log.warn("WebSocket 连接错误: ", cause);
    }

    /**
     * 发送消息
     *
     * @param session    接收者的会话
     * @param messageDTO 消息数据
     * @param error      错误信息
     */
    private static void sendMessage(Session session,
                                    WebSocketMessageDTO messageDTO, String error) {
        synchronized (weakInterner.intern(session.getId())) {
            try {
                session.getBasicRemote().sendText(ConvertUtils.getJson(messageDTO));
            } catch (IOException cause) {
                throw new RuntimeException(error, cause);
            }
        }
    }

    /**
     * 广播消息
     *
     * @param messageDTO 消息数据
     */
    public static void broadcastMessage(WebSocketMessageDTO messageDTO) {
        for (WebSocketHandler receiver : WEB_SOCKET_HANDLER_SET) {
            sendMessage(receiver.session, messageDTO, "广播消息异常");
        }
    }

}

