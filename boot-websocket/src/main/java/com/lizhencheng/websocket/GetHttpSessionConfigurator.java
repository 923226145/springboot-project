package com.lizhencheng.websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Created by lzc
 * 2020/4/11 16:59
 * 用来获取HttpSession
 */
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        // 设置 HttpSession ，以便后续取出
        config.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }
}
