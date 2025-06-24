package com.telegram_lite.controller;

import com.google.gson.Gson;
import com.telegram_lite.dto.GroupDto;
import com.telegram_lite.entity.ChatGroup;
import com.telegram_lite.entity.User;
import com.telegram_lite.service.GroupChatService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class GroupListServlet extends HttpServlet {
    private final GroupChatService groupChatService = new GroupChatService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            return;
        }

        String username = (String) session.getAttribute("username");
        List<ChatGroup> groups = groupChatService.findGroupsForUser(username);

        // Chuyển đổi List<ChatGroup> thành List<GroupDto>
        List<GroupDto> groupDtos = groups.stream()
                .map(group -> new GroupDto(
                        group.getId(),
                        group.getGroupName(),
                        group.getMembers().stream().map(User::getUsername).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(gson.toJson(groupDtos));
        out.flush();
    }
}