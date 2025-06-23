package com.telegram_lite.service; // Package đã cập nhật

import com.telegram_lite.dao.UserDao;
import com.telegram_lite.dao.UserDaoImpl; // Sử dụng implementation cụ thể
import com.telegram_lite.entity.User;
import org.mindrot.jbcrypt.BCrypt; // Import thư viện BCrypt
import com.telegram_lite.dto.UserDto;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.List;

public class UserService {

    private UserDao userDao;

    public UserService() {
        // Trong một ứng dụng lớn hơn, bạn có thể sử dụng Dependency Injection (ví dụ: Spring)
        // ở đây chúng ta khởi tạo trực tiếp
        this.userDao = new UserDaoImpl();
    }

    // Constructor cho phép inject UserDao (hữu ích cho testing)
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Đăng ký một người dùng mới.
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu (chưa hash)
     * @param displayName Tên hiển thị
     * @param email Email
     * @return Optional chứa User nếu đăng ký thành công, hoặc Optional.empty() nếu lỗi (ví dụ: username/email đã tồn tại)
     * @throws IllegalArgumentException nếu đầu vào không hợp lệ
     */
    public Optional<User> registerUser(String username, String password, String displayName, String email) throws IllegalArgumentException {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.isEmpty() ||
                displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username, password, and display name cannot be empty.");
        }

        // Kiểm tra xem username đã tồn tại chưa
        if (userDao.findUserByUsername(username).isPresent()) {
            System.err.println("Username " + username + " already exists."); // Nên dùng logger
            return Optional.empty();
        }

        // Kiểm tra xem email đã tồn tại chưa (nếu email được cung cấp)
        if (email != null && !email.trim().isEmpty() && userDao.findUserByEmail(email).isPresent()) {
            System.err.println("Email " + email + " already exists."); // Nên dùng logger
            return Optional.empty();
        }

        // Hash mật khẩu bằng BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setPassword(hashedPassword); // Lưu mật khẩu đã hash
        newUser.setDisplayName(displayName.trim());
        if (email != null && !email.trim().isEmpty()) {
            newUser.setEmail(email.trim());
        }
        // Các trường khác như status, avatarUrl có thể được set mặc định hoặc sau này

        try {
            userDao.saveUser(newUser);
            return Optional.of(newUser);
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Optional.empty();
        }
    }

    /**
     * Xác thực thông tin đăng nhập của người dùng.
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu (chưa hash)
     * @return Optional chứa User nếu đăng nhập thành công, ngược lại là Optional.empty()
     */
    public Optional<User> loginUser(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.isEmpty()) {
            System.err.println("Username or password cannot be empty for login.");
            return Optional.empty();
        }

        Optional<User> userOptional = userDao.findUserByUsername(username.trim());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Kiểm tra mật khẩu đã hash với mật khẩu người dùng cung cấp
            if (BCrypt.checkpw(password, user.getPassword())) {
                // Mật khẩu khớp
                return Optional.of(user);
            } else {
                // Sai mật khẩu
                System.err.println("Incorrect password for user: " + username);
                return Optional.empty();
            }
        } else {
            // Người dùng không tồn tại
            System.err.println("User not found: " + username);
            return Optional.empty();
        }
    }

    /**
     * Lấy thông tin chi tiết người dùng bằng ID.
     * @param userId ID người dùng
     * @return Optional<User>
     */
    public Optional<User> getUserDetails(Long userId) {
        return userDao.findUserById(userId);
    }

    // Các phương thức khác có thể thêm vào sau:
    // - updateUserProfile(User user)
    // - changePassword(Long userId, String oldPassword, String newPassword)
    // - deactivateUser(Long userId)
    public List<UserDto> searchUserDtos(String searchTerm, String excludeUsername) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>(); // Không tìm kiếm nếu searchTerm rỗng, trả về danh sách trống
        }

        // Gọi phương thức searchUsers hiệu quả từ DAO
        List<User> foundUsers = userDao.searchUsers(searchTerm.trim(), excludeUsername);

        // Chuyển đổi List<User> thành List<UserDto> để trả về client
        return foundUsers.stream()
                .map(user -> new UserDto(user.getUsername(), user.getDisplayName()))
                .collect(Collectors.toList());
    }

    // Bạn vẫn có thể giữ lại phương thức getAllUserDtos nếu cần
    public List<UserDto> getAllUserDtos(String excludeUsername) {
        return userDao.findAllUsers().stream()
                .filter(user -> !user.getUsername().equals(excludeUsername))
                .map(user -> new UserDto(user.getUsername(), user.getDisplayName()))
                .collect(Collectors.toList());
    }
}