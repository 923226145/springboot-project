package com.lizhencheng.websocket;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lzc
 * 2020/4/11 16:52
 */
@ServerEndpoint(value = "/websocket", configurator = GetHttpSessionConfigurator.class)
@Component
public class MyWebSocket {
    private Session session;
    private String username;
    private static Set<MyWebSocket> webSockets = new HashSet<>();
    /**
     * 建立连接
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        // 设置WebSocket的会话信息
        this.session = session;
        // 设置当前登录用户的HttpSession
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        if (httpSession.getAttribute("username") != null) {
            this.username = (String) httpSession.getAttribute("username");
            // 判断用户是否已经在线
            if (!userIsOnline(username)) {
                webSockets.add(this);
                System.out.println(username + "加入！ 当前在线人数：" + getOnlineUsers().size());
                // 组装消息
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("type","2");
                messageMap.put("fromName",username);
                messageMap.put("content",username + "已上线");
                messageMap.put("users",getOnlineUsers());
                pushMessage(messageMap, null);
            } else {
                // 用户已经在线的
                webSockets.add(this);
                System.out.println(username + "加入！ 当前在线人数：" + getOnlineUsers().size());
                // 此时只给当前链接发送消息
                // 组装消息
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("type","2");
                messageMap.put("fromName",username);
                messageMap.put("content",username + "已上线");
                messageMap.put("users",getOnlineUsers());
                try {
                    session.getBasicRemote().sendText(JSONObject.toJSONString(messageMap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 收到客户端的消息
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 组装消息
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("type","1");
        messageMap.put("fromName",this.username);
        messageMap.put("content",message);
        pushMessage(messageMap, null);
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        // 此时只给当前链接发送消息
        // 组装消息
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("type","2");
        messageMap.put("fromName",username);
        messageMap.put("content",username + "已下线");
        Set<String> users = getOnlineUsers();
        users.remove(username);
        messageMap.put("users",users);

        webSockets.remove(this);
        // 发送消息给其他在线用户
        for (MyWebSocket socket : webSockets) {
            try {
                if (!username.equals(socket.username)) {
                    socket.session.getBasicRemote().sendText(JSONObject.toJSONString(messageMap));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(username + "已下线！ 当前在线人数" + getOnlineUsers().size());
    }

    private boolean userIsOnline(String username) {
        boolean isOnline = false;
        for (MyWebSocket socket : webSockets) {
            if (socket.username.equals(username)) {
                isOnline = true;
                break;
            }
        }
        return isOnline;
    }

    /**
     * 获取当前用户列表
     * @return
     */
    private Set<String> getOnlineUsers() {
        Set<String> users = new HashSet<>();
        for (MyWebSocket socket : webSockets) {
            users.add(socket.username);
        }
        return users;
    }

    private void pushMessage(Map<String, Object> message, String toName) {
        // 发送所有人
        if (toName == null || "".equals(toName)) {
            for (MyWebSocket socket : webSockets) {
                try {
                    socket.session.getBasicRemote().sendText(JSONObject.toJSONString(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {  // 发送一个人
            for (MyWebSocket socket : webSockets) {
                try {
                    if (socket.username.equals(toName)) {
                        socket.session.getBasicRemote().sendText(JSONObject.toJSONString(message));
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}