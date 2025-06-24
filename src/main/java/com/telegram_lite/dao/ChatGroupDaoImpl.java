package com.telegram_lite.dao;

import com.telegram_lite.config.HibernateUtil;
import com.telegram_lite.entity.ChatGroup;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ChatGroupDaoImpl implements ChatGroupDao {

    @Override
    public ChatGroup save(ChatGroup group) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(group);
            transaction.commit();
            return group;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            // Cân nhắc throw một exception ở đây
            return null;
        }
    }

    @Override
    public Optional<ChatGroup> findById(Long groupId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ChatGroup group = session.get(ChatGroup.class, groupId);
            return Optional.ofNullable(group);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<ChatGroup> findGroupsByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sử dụng HQL để truy vấn các nhóm mà user là thành viên
            // 'g.members' là collection các thành viên trong entity ChatGroup 'g'
            // 'm' là một thành viên trong collection đó.
            Query<ChatGroup> query = session.createQuery(
                    "SELECT g FROM ChatGroup g JOIN g.members m WHERE m.id = :userId", ChatGroup.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            // Trả về danh sách rỗng nếu có lỗi
            return List.of();
        }
    }
}