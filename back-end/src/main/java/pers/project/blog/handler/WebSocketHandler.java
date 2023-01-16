package pers.project.blog.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.enumeration.ChatTypeEnum;
import pers.project.blog.dto.ChatRecordDTO;
import pers.project.blog.dto.RecalledMessageDTO;
import pers.project.blog.dto.WebSocketMessageDTO;
import pers.project.blog.entity.ChatRecordEntity;
import pers.project.blog.service.ChatRecordService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.SecurityUtils;

import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


/**
 * WebSocket 处理程序
 *
 * @author Luo Fei
 * @date 2023/1/12
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ServerEndpoint(value = "/websocket", configurator = WebSocketHandler.ChatConfigurator.class)
public class WebSocketHandler {

    /**
     * {@link ConcurrentHashMap} 支持的线程安全 Set，用来存放每个客户端对应的 WebSocketHandler 对象
     */
    private static final Set<WebSocketHandler>
            WEB_SOCKET_HANDLER_SET = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * 聊天记录服务
     */
    private static ChatRecordService chatRecordService;
    /**
     * 本类对象实例锁，用于保证线程安全的同步消息发送
     */
    private final ReentrantLock lock = new ReentrantLock();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 广播消息
     *
     * @param messageDTO 消息数据
     * @throws IOException IO 异常
     */
    public static void broadcastMessage(WebSocketMessageDTO messageDTO) throws IOException {
        for (WebSocketHandler webSocketHandler : WEB_SOCKET_HANDLER_SET) {
            webSocketHandler.lock.lock();
            try {
                webSocketHandler.session.getBasicRemote()
                        .sendText(ConversionUtils.getJson(messageDTO));
            } finally {
                webSocketHandler.lock.unlock();
            }
        }
    }

    @Autowired
    public ChatRecordService getChatRecordService() {
        return chatRecordService;
    }

    /**
     * 连接建立成功调用的方法
     *
     * @param session        与某个客户端的连接会话
     * @param endpointConfig 服务端点配置
     * @throws IOException IO 异常
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) throws IOException {
        // 加入连接
        this.session = session;
        WEB_SOCKET_HANDLER_SET.add(this);

        // 异步更新在线人数
        chatRecordService.UpdateOnlineCount(WEB_SOCKET_HANDLER_SET.size());

        // 加载聊天记录
        String ipAddress = endpointConfig
                .getUserProperties().get(ChatConfigurator.HEADER_NAME).toString();
        ChatRecordDTO chatRecordDTO = chatRecordService.getChatRecord(ipAddress);

        // 发送消息
        WebSocketMessageDTO messageDTO = WebSocketMessageDTO.builder()
                .type(ChatTypeEnum.HISTORY_RECORD.getType())
                .data(chatRecordDTO)
                .build();
        lock.lock();
        try {
            session.getBasicRemote().sendText(ConversionUtils.getJson(messageDTO));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param session 与某个客户端的连接会话
     * @param message 客户端发送过来的消息
     * @throws IOException IO 异常
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        WebSocketMessageDTO messageDTO = ConversionUtils.parseJson
                (message, WebSocketMessageDTO.class);
        switch (Objects.requireNonNull
                (ChatTypeEnum.get(messageDTO.getType()), "消息类型错误")) {
            case SEND_MESSAGE:
                // 发送的消息
                ChatRecordEntity chatRecordEntity = ConversionUtils.parseJson
                        (ConversionUtils.getJson(messageDTO.getData()), ChatRecordEntity.class);
                // 过滤 HTML 标签
                chatRecordEntity.setContent
                        (SecurityUtils.filter(chatRecordEntity.getContent()));
                chatRecordService.save(chatRecordEntity);
                // 广播消息
                broadcastMessage(messageDTO);
                break;
            case RECALL_MESSAGE:
                // 撤回的消息
                RecalledMessageDTO recalledMessageDTO = ConversionUtils.parseJson
                        (ConversionUtils.getJson(messageDTO.getData()), RecalledMessageDTO.class);
                // 删除消息记录
                chatRecordService.removeById(recalledMessageDTO.getId());
                // 广播消息
                broadcastMessage(messageDTO);
                break;
            case HEART_BEAT:
                // 心跳消息
                // TODO: 2023/1/13 魔法值
                messageDTO.setData("pong");
                lock.lock();
                try {
                    session.getBasicRemote().sendText(ConversionUtils.getJson(messageDTO));
                } finally {
                    lock.unlock();
                }
                break;
        }
    }

    /**
     * 连接关闭调用的方法
     *
     * @throws IOException IO 异常
     */
    @OnClose
    public void onClose() throws IOException {
        // 更新在线人数
        WEB_SOCKET_HANDLER_SET.remove(this);
        // TODO: 2023/1/13 异步
        // 异步更新在线人数
        chatRecordService.UpdateOnlineCount(WEB_SOCKET_HANDLER_SET.size());
    }

    /**
     * 获取客户端真实 IP
     */
    public static class ChatConfigurator extends ServerEndpointConfig.Configurator {

        public static String HEADER_NAME = "X-Real-IP";

        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            try {
                String firstFoundHeader = request.getHeaders().get(HEADER_NAME.toLowerCase()).get(0);
                sec.getUserProperties().put(HEADER_NAME, firstFoundHeader);
            } catch (Exception e) {
                log.warn("获取 IP 失败：", e);
                sec.getUserProperties().put(HEADER_NAME, "未知 IP");
            }
        }
    }

}
