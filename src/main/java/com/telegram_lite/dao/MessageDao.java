package com.telegram_lite.dao;

import com.telegram_lite.entity.Message;
import com.telegram_lite.entity.User;

import java.util.List;

public interface MessageDao {

    Message saveMessage(Message message);

    // Lấy lịch sử chat giữa hai người dùng, sắp xếp theo thời gian gần nhất trước
    // Có thể thêm phân trang sau này (limit, offset)
    List<Message> getMessagesBetweenUsers(User user1, User user2, int limit, int offset);
    List<Message> getMessagesForGroup(Long groupId);

    // (Tùy chọn cho tương lai)
    // void markMessagesAsRead(User sender, User receiver);
    // long countUnreadMessages(User receiver, User sender);
}