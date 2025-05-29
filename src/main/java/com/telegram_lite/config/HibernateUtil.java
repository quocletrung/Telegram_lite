package com.telegram_lite.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            // Tạo đối tượng Configuration từ hibernate.cfg.xml
            Configuration configuration = new Configuration().configure(); // Mặc định sẽ tìm file hibernate.cfg.xml

            configuration.addAnnotatedClass(com.telegram_lite.entity.User.class);
            configuration.addAnnotatedClass(com.telegram_lite.entity.Message.class);

            // Hibernate 5.x trở lên yêu cầu ServiceRegistry
            // Chúng ta sẽ đăng ký các Entity ở đây sau khi tạo chúng
            // Ví dụ: configuration.addAnnotatedClass(com.yourproject.entity.User.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            System.out.println("Hibernate SessionFactory created successfully and entities registered."); // Thêm log này


        } catch (Throwable ex) {
            // Ghi lại lỗi. Vì đây là lỗi nghiêm trọng khi khởi tạo SessionFactory.
            System.err.println("Initial SessionFactory creation failed. " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Đóng caches và connection pools
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
