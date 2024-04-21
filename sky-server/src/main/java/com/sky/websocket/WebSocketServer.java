package com.sky.websocket;

import org.springframework.stereotype.Component;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket服务
 */
@Component
/*
* 根据前端路径进行匹配
* 登录管理端后台，控制台前端会向服务器发送 ws:://localhost/ws/{sid}, 之所以路径中没有8080端口，是因为ngix对访问路径进行了重映射
* */
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {

    /*
    * 存放会话对象
    * client 与 server 端使用 session 保持连接
    * */
    private static Map<String, Session> sessionMap = new HashMap();

    /**
     * 回调函数
     * 连接建立成功时调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        System.out.println("客户端：" + sid + "建立连接");
        sessionMap.put(sid, session);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        System.out.println("收到来自客户端：" + sid + "的信息:" + message);
    }

    /**
     * 连接关闭调用的方法
     *
     * @param sid
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        System.out.println("连接断开:" + sid);
        sessionMap.remove(sid);
    }

    /**
     * 群发
     *
     * @param message
     */
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                /*
                * 服务器向客户端发送消息
                * webSocket不同于http的地方：
                *   webSocket可以主动向客户端发消息
                *   而http是基于请求响应，客户端请求，服务端才会发消息，服务端不会主动，只会等客户端舔
                * */
                session.getBasicRemote().sendText(message);  // 遍历所有session，给所有的客户端发消息，得到群发的效果
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
