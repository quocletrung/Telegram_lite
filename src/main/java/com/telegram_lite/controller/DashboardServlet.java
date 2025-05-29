package com.telegram_lite.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Lấy session hiện tại, không tạo mới

        // Kiểm tra xem người dùng đã đăng nhập chưa (bằng cách kiểm tra attribute trong session)
        if (session != null && session.getAttribute("loggedInUser") != null) {
            // Người dùng đã đăng nhập, cho phép truy cập dashboard
            request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
        } else {
            // Người dùng chưa đăng nhập, chuyển hướng về trang login
            request.setAttribute("errorMessage", "Please login to access this page.");
            // Thay vì forward, chúng ta có thể redirect để URL sạch hơn
            response.sendRedirect(request.getContextPath() + "/login?error=Please+login+to+access+this+page.");
        }
    }
}