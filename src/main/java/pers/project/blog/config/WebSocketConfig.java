package pers.project.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.server.ServerEndpoint;

/**
 * WebSocket 配置类
 *
 * @author Luo Fei
 * @date 2023/1/12
 */
@Configuration
public class WebSocketConfig {

    /**
     * 检测带有 {@link ServerEndpoint} 注解的 Bean 并注册它们
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
