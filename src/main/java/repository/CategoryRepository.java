package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import entity.Category;
import util.JPAUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CategoryRepository - Lớp truy cập dữ liệu cho Category
 * 
 * Chứa các phương thức để:
 * - Tìm, lưu, cập nhật Category
 * - Kiểm tra ràng buộc (Category có Product active không?)
 */
public class CategoryRepository {

    /**
     * Lấy tất cả danh mục (không bao gồm xóa mềm vì Category không có soft delete)
     */
    public List<Category> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c ORDER BY c.createdAt DESC", Category.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("❌ Lỗi lấy tất cả Category: " + e.getMessage());
            return new ArrayList<>();  // Java 8 compatible
        } finally {
            em.close();
        }
    }

    /**
     * Tìm danh mục theo ID
     * 
     * @param id: ID danh mục
     * @return: Optional<Category>
     */
    public Optional<Category> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Category category = em.find(Category.class, id);
            return Optional.ofNullable(category);
        } catch (Exception e) {
            System.err.println("❌ Lỗi tìm Category by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Kiểm tra Category có Product active nào không?
     * 
     * Dùng để: Không cho disable Category nếu còn Product active
     * Ràng buộc này dùng trong Business Logic (Service layer)
     * 
     * @param categoryId: ID danh mục
     * @return: true nếu có Product active
     */
    public boolean hasActiveProducts(Long categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Đếm số lượng Product active của Category này
            String jpql = "SELECT COUNT(p) FROM Product p " +
                         "WHERE p.category.id = :categoryId " +
                         "AND p.status = true AND p.deleted = false";
            
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("categoryId", categoryId);
            
            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            System.err.println("❌ Lỗi kiểm tra Product active: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Lưu danh mục mới
     * 
     * @param category: Danh mục cần lưu
     * @return: Danh mục đã lưu
     */
    public Category save(Category category) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(category);
            em.getTransaction().commit();
            return category;
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("❌ Lỗi lưu Category: " + e.getMessage());
            throw new RuntimeException("Lỗi lưu danh mục: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật danh mục
     * 
     * @param category: Danh mục cần cập nhật
     * @return: Danh mục đã cập nhật
     */
    public Category update(Category category) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Category merged = em.merge(category);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("❌ Lỗi cập nhật Category: " + e.getMessage());
            throw new RuntimeException("Lỗi cập nhật danh mục: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}
