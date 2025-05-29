package com.telegram_lite.controller;

import com.telegram_lite.entity.User;
import com.telegram_lite.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Import HttpSession

import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
    }

    /**
     * Hiển thị form đăng nhập.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra xem có thông báo thành công từ trang đăng ký không
        HttpSession session = request.getSession(false); // false: không tạo session mới nếu chưa có
        if (session != null && session.getAttribute("successMessage") != null) {
            request.setAttribute("successMessage", session.getAttribute("successMessage"));
            session.removeAttribute("successMessage"); // Xóa thông báo sau khi đã lấy
        }
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    /**
     * Xử lý yêu cầu đăng nhập.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() ||
                password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required.");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            return;
        }

        Optional<User> userOptional = userService.loginUser(username, password);

        if (userOptional.isPresent()) {
            // Đăng nhập thành công
            User user = userOptional.get();

            // Tạo hoặc lấy HttpSession hiện tại
            HttpSession session = request.getSession(true); // true: tạo session mới nếu chưa có

            // Lưu thông tin người dùng vào session
            // Chỉ lưu những thông tin cần thiết và không nhạy cảm
            session.setAttribute("loggedInUser", user.getUsername()); // Ví dụ: lưu username
            session.setAttribute("userId", user.getId());       // Ví dụ: lưu ID người dùng
            session.setAttribute("displayName", user.getDisplayName()); // Ví dụ: lưu tên hiển thị

            // Đặt thời gian timeout cho session (ví dụ: 30 phút)
            session.setMaxInactiveInterval(30 * 60);

            // Chuyển hướng đến trang dashboard hoặc trang chat (chúng ta sẽ tạo sau)
            // Ví dụ: chuyển hướng đến một servlet "/dashboard"
            response.sendRedirect(request.getContextPath() + "/dashboard"); // Hoặc "/chat"
        } else {
            // Đăng nhập thất bại
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.setAttribute("username", username); // Giữ lại username đã nhập
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }
}