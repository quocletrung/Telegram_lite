package com.telegram_lite.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.telegram_lite.entity.ChatGroup;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.telegram_lite.service.GroupChatService;


public class CreateGroupServlet extends HttpServlet {
    private final GroupChatService groupChatService = new GroupChatService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String creatorUsername = (String) session.getAttribute("username");

        // Đọc JSON payload từ body của request
        String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Map<String, Object> params = gson.fromJson(body, new TypeToken<Map<String, Object>>(){}.getType());

        String groupName = (String) params.get("groupName");
        List<String> memberUsernames = (List<String>) params.get("members");

        if (groupName == null || groupName.isBlank() || memberUsernames == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            resp.getWriter().write("Group name and members are required.");
            return;
        }

        ChatGroup createdGroup = groupChatService.createGroup(groupName, creatorUsername, memberUsernames);

        if (createdGroup != null) {
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            out.print(gson.toJson(Map.of("id", createdGroup.getId(), "groupName", createdGroup.getGroupName())));
            out.flush();
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Failed to create group.");
        }
    }
}