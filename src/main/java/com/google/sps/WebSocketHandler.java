package com.google.sps;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.websocket.Session;

public class WebSocketHandler {

    private static WebSocketHandler instance;

    // Map (long)roomId -> List<Session> list of sessions active in this room.
    private HashMap<Long, List<Session>> chatRoomMap;

    private WebSocketHandler() {
        chatRoomMap = new HashMap<Long, List<Session>>();
    }

    /**
     * Resets the unique instance of the class.
     * Visible for testing
     */
    public static void resetInstance() {
      instance = null;
    }

    public static WebSocketHandler getInstance() {
        if (instance == null) {
            instance = new WebSocketHandler();
        }

        return instance;
    }

    public void addSession(long roomId, Session session) {
        if (!chatRoomMap.containsKey(roomId)) {
            chatRoomMap.put(roomId, new ArrayList<Session>());
        }

        chatRoomMap.get(roomId).add(session);
    }

    public List<Session> getRoomList(long roomId) {
        if (chatRoomMap.containsKey(roomId)) {
            return Collections.unmodifiableList(
                chatRoomMap.get(roomId));
        }

        return null;
    }

    public void removeSession(long roomId, Session session) {
        List<Session> roomList = chatRoomMap.get(roomId);

        roomList.remove(session);

        if (roomList.isEmpty()) {
            chatRoomMap.remove(roomId);
        }
    }
}
