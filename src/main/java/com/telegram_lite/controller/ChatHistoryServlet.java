package com.telegram_lite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.telegram_lite.entity.Message;
import com.telegram_lite.service.MessageService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


@WebServlet("/chatHistory")
public class ChatHistoryServlet extends HttpServlet {

    private MessageService messageService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.messageService = new MessageService();
        this.objectMapper = new ObjectMapper();
        // Đăng ký module để ObjectMapper có thể serialize/deserialize Java 8 Time (LocalDateTime)
        this.objectMapper.registerModule(new JavaTimeModule());
        // Cấu hình để không ném lỗi nếu có thuộc tính không xác định trong JSON (ít quan trọng khi serialize)
        // this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Cấu hình định dạng ngày giờ nếu muốn nhất quán (server đã làm khi gửi qua WS)
        // this.objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not authenticated.");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }

        String loggedInUsername = (String) session.getAttribute("loggedInUser");
        String partnerUsername = request.getParameter("partnerUsername");

        if (partnerUsername == null || partnerUsername.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Partner username is required.");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }

        // Lấy tham số phân trang (ví dụ mặc định)
        int page = 0; // Trang đầu tiên
        int pageSize = 50; // Lấy 50 tin nhắn gần nhất

        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                // Bỏ qua, dùng giá trị mặc định
            }
        }
        String pageSizeParam = request.getParameter("pageSize");
        if (pageSizeParam != null) {
            try {
                pageSize = Integer.parseInt(pageSizeParam);
                if (pageSize > 100) pageSize = 100; // Giới hạn kích thước trang
            } catch (NumberFormatException e) {
                // Bỏ qua, dùng giá trị mặc định
            }
        }

        List<Message> chatHistory = messageService.getChatHistory(loggedInUsername, partnerUsername, page, pageSize);

        // Chuyển đổi danh sách Message thành một định dạng phù hợp hơn cho client nếu cần
        // Ví dụ: chỉ gửi các trường cần thiết, định dạng lại timestamp
        // Hiện tại, objectMapper sẽ serialize toàn bộ đối tượng Message (có thể bao gồm cả đối tượng User lồng nhau)
        // Điều này có thể không tối ưu và có thể lộ thông tin không cần thiết.
        // Nên tạo DTOs (Data Transfer Objects) cho việc này.
        // Tạm thời, chúng ta sẽ serialize trực tiếp.

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(objectMapper.writeValueAsString(chatHistory));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error processing chat history.");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}