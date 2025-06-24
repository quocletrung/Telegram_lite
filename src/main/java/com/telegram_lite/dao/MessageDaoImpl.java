package com.telegram_lite.dao;

import com.telegram_lite.config.HibernateUtil;
import com.telegram_lite.entity.Message;
import com.telegram_lite.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class MessageDaoImpl implements MessageDao {

    @Override
    public Message saveMessage(Message message) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(message); // Lưu đối tượng message
            transaction.commit();
            return message; // Trả về message đã được lưu (có thể có ID được gán)
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
            return null; // Hoặc ném ra một ngoại lệ tùy chỉnh
        }
    }
    @Override
    public List<Message> getMessagesForGroup(Long groupId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Message> query = session.createQuery(
                    "FROM Message WHERE receiverGroup.id = :groupId ORDER BY timestamp ASC", Message.class
            );
            query.setParameter("groupId", groupId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Trả về danh sách rỗng nếu có lỗi
        }
    }

    @Override
    public List<Message> getMessagesBetweenUsers(User user1, User user2, int limit, int offset) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Lấy tin nhắn mà user1 gửi cho user2 HOẶC user2 gửi cho user1
            // Sắp xếp theo thời gian giảm dần (tin nhắn mới nhất lên đầu)
            String hql = "FROM Message m WHERE " +
                    "(m.sender = :user1 AND m.receiver = :user2) OR " +
                    "(m.sender = :user2 AND m.receiver = :user1) " +
                    "ORDER BY m.timestamp DESC";
            Query<Message> query = session.createQuery(hql, Message.class);
            query.setParameter("user1", user1);
            query.setParameter("user2", user2);

            // Thiết lập phân trang
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            if (offset >= 0) { // offset có thể là 0 cho trang đầu tiên
                query.setFirstResult(offset);
            }

            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return new ArrayList<>(); // Trả về danh sách rỗng nếu có lỗi
        }
    }
}