package com.telegram_lite.service;

import com.telegram_lite.dao.MessageDao;
import com.telegram_lite.dao.MessageDaoImpl;
import com.telegram_lite.dao.UserDao;
import com.telegram_lite.dao.UserDaoImpl;
import com.telegram_lite.entity.Message;
import com.telegram_lite.entity.MessageType;
import com.telegram_lite.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MessageService {

    private MessageDao messageDao;
    private UserDao userDao;

    public MessageService() {
        this.messageDao = new MessageDaoImpl();
        this.userDao = new UserDaoImpl(); // Cần UserDao để lấy thông tin User
    }

    // Constructor cho phép inject (hữu ích cho testing)
    public MessageService(MessageDao messageDao, UserDao userDao) {
        this.messageDao = messageDao;
        this.userDao = userDao;
    }

    /**
     * Tạo và lưu một tin nhắn mới.
     *
     * @param senderUsername   Username của người gửi.
     * @param receiverUsername Username của người nhận.
     * @param messageType      Loại tin nhắn (TEXT, IMAGE, VIDEO).
     * @param content          Nội dung text hoặc caption cho media.
     * @param mediaUrl         URL của media (nếu có, null nếu là TEXT).
     * @return Optional chứa Message đã lưu, hoặc Optional.empty() nếu có lỗi (ví dụ: không tìm thấy người dùng).
     */
    public Optional<Message> createAndSaveMessage(String senderUsername, String receiverUsername,
                                                  MessageType messageType, String content, String mediaUrl) {
        Optional<User> senderOpt = userDao.findUserByUsername(senderUsername);
        Optional<User> receiverOpt = userDao.findUserByUsername(receiverUsername);

        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            System.err.println("Sender or Receiver not found. Message not saved.");
            return Optional.empty();
        }

        User sender = senderOpt.get();
        User receiver = receiverOpt.get();

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessageType(messageType);
        message.setContent(content); // Có thể là null nếu chỉ có mediaUrl và không có caption
        message.setMediaUrl(mediaUrl); // Sẽ là null nếu là tin nhắn TEXT
        // timestamp và isRead sẽ được tự động xử lý hoặc có giá trị mặc định

        Message savedMessage = messageDao.saveMessage(message);
        return Optional.ofNullable(savedMessage);
    }

    /**
     * Lấy lịch sử chat giữa hai người dùng.
     *
     * @param user1Username Username của người dùng thứ nhất.
     * @param user2Username Username của người dùng thứ hai.
     * @param page          Số trang (ví dụ: 0 cho trang đầu tiên).
     * @param pageSize      Số lượng tin nhắn mỗi trang.
     * @return Danh sách các tin nhắn.
     */
    public List<Message> getChatHistory(String user1Username, String user2Username, int page, int pageSize) {
        Optional<User> user1Opt = userDao.findUserByUsername(user1Username);
        Optional<User> user2Opt = userDao.findUserByUsername(user2Username);

        if (user1Opt.isPresent() && user2Opt.isPresent()) {
            int offset = page * pageSize;
            return messageDao.getMessagesBetweenUsers(user1Opt.get(), user2Opt.get(), pageSize, offset);
        }
        return Collections.emptyList(); // Trả về danh sách rỗng nếu không tìm thấy người dùng
    }
}