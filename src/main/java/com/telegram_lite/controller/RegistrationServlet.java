package com.telegram_lite.controller; // Package đã cập nhật

import com.telegram_lite.entity.User;
import com.telegram_lite.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

// Annotation @WebServlet để khai báo Servlet thay vì dùng web.xml
// "/register" là URL pattern mà Servlet này sẽ xử lý
@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        // Khởi tạo UserService khi Servlet được load lần đầu
        // Đây là cách đơn giản, trong ứng dụng lớn có thể dùng Dependency Injection
        super.init();
        userService = new UserService();
    }

    /**
     * Xử lý GET request. Thường dùng để hiển thị form đăng ký.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển hướng đến trang JSP hiển thị form đăng ký
        // Chúng ta sẽ tạo file register.jsp sau
        request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
    }

    /**
     * Xử lý POST request. Nhận dữ liệu từ form đăng ký và xử lý.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy các tham số từ form
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword"); // Thêm trường xác nhận mật khẩu
        String displayName = request.getParameter("displayName");
        String email = request.getParameter("email");

        // Validate đầu vào (đơn giản)
        if (username == null || username.trim().isEmpty() ||
                password == null || password.isEmpty() ||
                displayName == null || displayName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username, password, and display name are required.");
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            request.setAttribute("username", username); // Giữ lại giá trị đã nhập
            request.setAttribute("displayName", displayName);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
            return;
        }

        try {
            Optional<User> registeredUser = userService.registerUser(username, password, displayName, email);

            if (registeredUser.isPresent()) {
                // Đăng ký thành công, có thể lưu thông báo vào session và chuyển hướng đến trang đăng nhập
                request.getSession().setAttribute("successMessage", "Registration successful! Please login.");
                response.sendRedirect(request.getContextPath() + "/login"); // Chuyển đến trang login (sẽ tạo servlet /login sau)
            } else {
                // Đăng ký thất bại (ví dụ: username/email đã tồn tại)
                request.setAttribute("errorMessage", "Registration failed. Username or email might already exist.");
                request.setAttribute("username", username); // Giữ lại giá trị đã nhập
                request.setAttribute("displayName", displayName);
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            request.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(request, response);
        }
    }
}