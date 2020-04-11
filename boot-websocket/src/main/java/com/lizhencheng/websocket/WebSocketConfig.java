package com.lizhencheng.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Created by lzc
 * 2020/4/11 19:45
 */
@Configuration
public class WebSocketConfig {
    // 这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
