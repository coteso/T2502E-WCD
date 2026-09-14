package com.example.javawebmvc.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Khởi động EntityManagerFactory khi ứng dụng deploy (hbm2ddl tạo/sửa bảng)
 * và đóng sạch khi undeploy - tránh rò rỉ connection pool của MySQL.
 */
@WebListener
public class JpaLifecycleListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        JPAUtil.getEntityManagerFactory();
        sce.getServletContext().log("[JPA] EntityManagerFactory đã sẵn sàng (PU=productORMPU).");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JPAUtil.shutdown();
        sce.getServletContext().log("[JPA] EntityManagerFactory đã đóng.");
    }
}
