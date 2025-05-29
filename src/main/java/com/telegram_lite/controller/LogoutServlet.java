package com.telegram_lite.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Lấy session, không tạo mới

        if (session != null) {
            session.invalidate(); // Hủy session
        }

        // Chuyển hướng người dùng về trang đăng nhập
        // Có thể thêm một thông báo để cho biết đã đăng xuất thành công
        response.sendRedirect(request.getContextPath() + "/login");
    }
}