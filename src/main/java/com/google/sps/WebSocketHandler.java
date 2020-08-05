package com.google.sps;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;

public class WebSocketHandler {

    private static WebSocketHandler instance;

    private HashMap<String, List<Session>> chatRoomMap;

    private WebSocketHandler() {
        chatRoomMap = new HashMap<String, List<Session>>();
    }

    public static WebSocketHandler getInstance() {
        if (instance == null) {
            instance = new WebSocketHandler();
        }

        return instance;
    }

    public synchronized void addSession(String roomId, Session session) {
        if (!chatRoomMap.containsKey(roomId)) {
            chatRoomMap.put(roomId, new ArrayList<Session>());
        }

        chatRoomMap.get(roomId).add(session);
    }

    public List<Session> getRoomList(String roomId) {
        if (chatRoomMap.containsKey(roomId)) {
            return List.copyOf(chatRoomMap.get(roomId));
        }

        return null;
    }

    public synchronized void removeSession(String roomId, Session session) {
        List<Session> roomList = chatRoomMap.get(roomId);

        roomList.remove(session);

        if (roomList.isEmpty()) {
            chatRoomMap.remove(roomId);
        }
    }
}
