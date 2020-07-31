package com.google.sps.servlets;

import com.google.sps.CapstoneAuth;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/authentication")
public class AuthServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tokenId = request.getReader().readLine();

        String responseText = CapstoneAuth.isUserAuthenticated(tokenId).toString();

        response.setContentType("text/html");
        response.getWriter().println(responseText);
    }
}
