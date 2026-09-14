package com.example.javawebmvc.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * JPAUtil - quản lý vòng đời EntityManagerFactory/EntityManager cho toàn ứng dụng.
 *
 * - EMF tạo ĐÚNG 1 LẦN (rẻ nhất), dùng chung, thread-safe.
 * - EntityManager rẻ, KHÔNG thread-safe => mỗi thread/request một instance, luôn đóng trong finally.
 *
 * Cách dùng chuẩn trong Service/Repository:
 *   EntityManager em = JPAUtil.getEntityManager();
 *   try { ... } finally { em.close(); }
 * hoặc dùng các helper runInTransaction bên dưới để gói transaction tự động.
 */
public final class JPAUtil {

    private static final String PERSISTENCE_UNIT = "productORMPU";
    private static volatile EntityManagerFactory emf;

    private JPAUtil() {
    }

    /** Lấy EntityManagerFactory (lazy init, double-checked locking). */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            synchronized (JPAUtil.class) {
                if (emf == null) {
                    emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
                }
            }
        }
        return emf;
    }

    /** Mỗi request/luồng một EntityManager - caller phải đóng trong finally. */
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /** Gói một đoạn code vào transaction: begin -> work -> commit, rollback nếu ném exception. */
    public static void runInTransaction(java.util.function.Consumer<EntityManager> work) {
        EntityManager em = getEntityManager();
        jakarta.persistence.EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            work.accept(em);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Giống runInTransaction nhưng trả về kết quả từ work. */
    public static <T> T runInTransactionReturn(java.util.function.Function<EntityManager, T> work) {
        EntityManager em = getEntityManager();
        jakarta.persistence.EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T result = work.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Đóng EMF khi ứng dụng dừng (gọi từ ServletContextListener). */
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
