package com.huanshi.springboot.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author websocket服务
 */
@ServerEndpoint(value = "/roomServer/{roomId}/{uuid}")
@Component
public class WebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    private String roomId;

    /**
     * 标识当前连接客户端的id
     */
    private String uuid;

    private Boolean flag = false;

    /**
     *  与某个客户端的连接对话，需要通过它来给客户端发送消息
     */
    private Session session;

    /**
     * 记录当前在线连接数
     */
    private static final Map<String, ConcurrentHashMap<String,WebSocketServer>> rooms = new ConcurrentHashMap<>();


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam("roomId") String roomId, Session session, @PathParam("uuid") String uuid) {
        try{
            this.session = session;
            this.roomId = roomId;
            if (!rooms.containsKey(roomId)) {
                // 对应房间不存在时，创建房间
                ConcurrentHashMap<String,WebSocketServer> room = new ConcurrentHashMap<>();
                this.flag = true;
                // 添加用户
                room.put(uuid, this);
                this.uuid = uuid;
                rooms.put(roomId, room);
                this.roomId = roomId;
            } else {
                // 房间已存在，直接添加用户到相应的房间
                rooms.get(roomId).put(uuid, this);
            }
//            addOnlineCount();//在线数加1
//            log.info("有新连接加入！当前在线人数为" + getOnlineCount());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("roomId") String roomId,  @PathParam("uuid") String uuid) {
        ConcurrentHashMap<String, WebSocketServer> map = rooms.get(roomId);
        String masterId = null;
        for(String key:map.keySet()){
            WebSocketServer myWebSocket = map.get(key);
            if (myWebSocket.flag){
                masterId = key;
                break;
            }
        }
        if (uuid.equals(masterId)){
            rooms.remove(roomId);
        } else {
            rooms.get(roomId).remove(uuid);
        }

    }

    /**
     * 收到客户端消息后调用的方法
     * 后台收到客户端发送过来的消息
     * onMessage 是一个消息的中转站
     * 接受 浏览器端 socket.send 发送过来的 json数据
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        String roomId = this.roomId;
        broadcast(roomId, message);

    }
    public void broadcast(String roomId, String message) throws Exception {
        ConcurrentHashMap<String, WebSocketServer> map = rooms.get(roomId);
        String masterId = null;
        for(String key:map.keySet()){
            WebSocketServer myWebSocket = map.get(key);
            if (myWebSocket.flag){
                masterId = key;
                break;
            }
        }

        if (Objects.equals(this.uuid, masterId)){
            for(String key:map.keySet()){//keySet获取map集合key的集合  然后在遍历key即可
                try{
                    if (!Objects.equals(key, masterId)){
//                        System.out.println(message);
                        WebSocketServer myWebSocket = map.get(key);
                        myWebSocket.sendMessage(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }

    public Boolean selectRoom(String roomId){
        Set<String> roomIds = rooms.keySet();
        for (String nowRoomId: roomIds) {
            log.info(nowRoomId);
            if (nowRoomId.equals(roomId)){
                return false;
            }
        }
        return true;
    }
}
