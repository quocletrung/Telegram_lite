package com.telegram_lite.controller;

import com.telegram_lite.dao.UserDao;
import com.telegram_lite.dao.UserDaoImpl;
import com.telegram_lite.entity.User;
import com.telegram_lite.entity.UserStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        super.init();
        userDao = new UserDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Lấy session, không tạo mới

        if (session != null) {
            String username = (String) session.getAttribute("loggedInUser");
            if (username != null) {
                User user = userDao.findUserByUsername(username).orElse(null);
                if (user != null) {
                    user.setStatus(UserStatus.OFFLINE);
                    userDao.updateUser(user);
                }
            }
            session.invalidate(); // Hủy session
        }

        // Chuyển hướng người dùng về trang đăng nhập
        // Có thể thêm một thông báo để cho biết đã đăng xuất thành công
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}