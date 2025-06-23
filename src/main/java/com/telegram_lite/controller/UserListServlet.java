package com.telegram_lite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegram_lite.dto.UserDto;
import com.telegram_lite.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap; // Import HashMap
import java.util.Map; // Import Map

@WebServlet("/users")
public class UserListServlet extends HttpServlet {
    private UserService userService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not authenticated.");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }
        String loggedInUsername = (String) session.getAttribute("loggedInUser");

        String searchTerm = request.getParameter("search");
        List<UserDto> users;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            users = userService.searchUserDtos(searchTerm.trim(), loggedInUsername);
        } else {
            // Có thể trả về danh sách rỗng hoặc danh sách liên hệ gần đây
            // Tạm thời trả về danh sách rỗng nếu không có từ khóa tìm kiếm
            users = new ArrayList<>();
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), users);
    }
}