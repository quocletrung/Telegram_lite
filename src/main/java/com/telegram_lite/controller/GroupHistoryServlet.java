package com.telegram_lite.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.telegram_lite.entity.Message;
import com.telegram_lite.service.GroupChatService;

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

public class GroupHistoryServlet extends HttpServlet {
    private final GroupChatService groupChatService = new GroupChatService();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String groupIdStr = req.getParameter("groupId");
        if (groupIdStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("groupId is required.");
            return;
        }

        try {
            Long groupId = Long.parseLong(groupIdStr);
            List<Message> messages = groupChatService.getGroupHistory(groupId);

            // Chuyển đổi danh sách tin nhắn thành định dạng JSON đơn giản
            List<Object> result = messages.stream().map(msg -> Map.of(
                    "id", msg.getId(),
                    "content", msg.getContent(),
                    "sender", msg.getSender().getUsername(),
                    "timestamp", msg.getTimestamp(),
                    "messageType", msg.getMessageType().toString(),
                    "mediaUrl", msg.getMediaUrl() != null ? msg.getMediaUrl() : ""
            )).collect(Collectors.toList());

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            out.print(gson.toJson(result));
            out.flush();

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid groupId format.");
        }
    }
}