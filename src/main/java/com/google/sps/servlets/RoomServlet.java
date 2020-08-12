package com.google.sps.servlets;

import com.google.sps.CapstoneAuth;
import com.google.sps.DatastoreManager;
import com.google.sps.ChatWebSocket;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import java.io.IOException;

@WebServlet("/room-server")
public class RoomServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        JSONObject newUserData = new JSONObject(request.getReader().readLine());

        String tokenId = newUserData.getString(ChatWebSocket.JSON_ID);
        String roomName = newUserData.getString(ChatWebSocket.JSON_ROOM_NAME);

        if (!CapstoneAuth.isUserAuthenticated(tokenId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            System.out.println("Failed");
            return;
        }

        String uid = CapstoneAuth.getUserId(tokenId);

        DatastoreManager.getInstance().createChatRoom(roomName, uid);

        response.setContentType("text/html");
        response.getWriter().print(true);
    }
}
