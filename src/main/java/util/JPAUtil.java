package util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * JPAUtil - Tiện ích quản lý EntityManager
 * 
 * EntityManager: Đối tượng chính của JPA, dùng để:
 * - Persist (lưu) entities vào database
 * - Merge (cập nhật) entities
 * - Remove (xóa) entities
 * - Query (truy vấn) entities
 * 
 * EntityManagerFactory: Nhà máy tạo ra EntityManager (khởi tạo 1 lần)
 * 
 * Pattern: Singleton
 * - Chỉ tạo 1 EntityManagerFactory duy nhất cho cả ứng dụng
 * - Mỗi request/transaction tạo 1 EntityManager mới từ factory
 */
public class JPAUtil {
    
    // ===== STATIC INITIALIZATION =====
    // static: Tạo một lần khi class được load
    // final: Không thể thay đổi sau khi khởi tạo
    private static EntityManagerFactory emf;

    // Static block: Chạy 1 lần khi class được load lần đầu
    static {
        try {
            // "product_orm_mvc" là tên persistence-unit trong persistence.xml
            // Factory này sẽ đọc cấu hình (database connection, entities, v.v.) từ persistence.xml
            emf = Persistence.createEntityManagerFactory("product_orm_mvc");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khởi tạo EntityManagerFactory:");
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Lấy EntityManager mới
     * 
     * Cách dùng:
     * EntityManager em = JPAUtil.getEntityManager();
     * try {
     *     em.getTransaction().begin();
     *     em.persist(product);
     *     em.getTransaction().commit();
     * } finally {
     *     em.close();
     * }
     * 
     * @return EntityManager mới để thực hiện các thao tác
     */
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Đóng EntityManagerFactory khi ứng dụng kết thúc
     * Gọi method này trong shutdown hook hoặc destroy method của servlet
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
