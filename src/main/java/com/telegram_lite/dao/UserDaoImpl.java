package com.telegram_lite.dao; // Package đã cập nhật

import com.telegram_lite.config.HibernateUtil; // HibernateUtil đã cập nhật
import com.telegram_lite.entity.User;    // Entity User đã cập nhật
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    @Override
    public void saveUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user); // Lưu đối tượng user
            transaction.commit(); // Hoàn tất giao dịch
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Hoàn tác nếu có lỗi
            }
            e.printStackTrace(); // In lỗi ra console (nên dùng logger trong dự án thực tế)
        }
    }

    @Override
    public void updateUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(user); // Cập nhật đối tượng user
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(Long userId) {
        Transaction transaction = null;
        User user = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            user = session.get(User.class, userId); // Tìm user bằng ID
            if (user != null) {
                session.delete(user); // Xóa user
                System.out.println("User is deleted");
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, userId); // Lấy user bằng ID
            return Optional.ofNullable(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sử dụng HQL (Hibernate Query Language)
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            User user = query.uniqueResult(); // Lấy kết quả duy nhất
            return Optional.ofNullable(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("unchecked") // Sử dụng cho list() không generic của Hibernate cũ hơn, hoặc dùng getResultList()
    public List<User> findAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sử dụng HQL để lấy tất cả user
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    @Override
    public List<User> searchUsers(String searchTerm, String usernameToExclude) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sử dụng HQL với LIKE để tìm kiếm gần đúng, không phân biệt hoa thường
            // và loại trừ người dùng hiện tại
            String hql = "FROM User u WHERE " +
                    "(LOWER(u.username) LIKE :term OR LOWER(u.displayName) LIKE :term) " +
                    "AND u.username != :excludeUsername";

            Query<User> query = session.createQuery(hql, User.class);

            // Thêm dấu % vào searchTerm để tìm kiếm gần đúng
            query.setParameter("term", "%" + searchTerm.toLowerCase() + "%");
            query.setParameter("excludeUsername", usernameToExclude);

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Trả về danh sách rỗng nếu có lỗi
        }
    }
}