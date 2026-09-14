package service;

import entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import repository.CategoryRepository;
import util.JPAUtil;

import java.util.List;

public class CategoryService {

    private final CategoryRepository categoryRepository = new CategoryRepository();

    public List<Category> getAllCategories() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return categoryRepository.findAll(em);
        } finally {
            em.close();
        }
    }

    public Category getCategoryById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return categoryRepository.findById(id, em)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Danh mục có ID: " + id));
        } finally {
            em.close();
        }
    }

    public void saveCategory(String name, String description, Boolean status) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (categoryRepository.existsByName(name, null, em)) {
                throw new IllegalArgumentException("Tên danh mục '" + name.trim() + "' đã tồn tại.");
            }

            Category category = new Category(name.trim(), description != null ? description.trim() : null, status != null ? status : true);
            categoryRepository.save(category, em);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void updateCategory(Long id, String name, String description, Boolean status) {
        if (id == null) {
            throw new IllegalArgumentException("ID danh mục không hợp lệ.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category category = categoryRepository.findById(id, em)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục để cập nhật."));

            if (categoryRepository.existsByName(name, id, em)) {
                throw new IllegalArgumentException("Tên danh mục '" + name.trim() + "' đã bị trùng.");
            }

            // Check if disabling category with active products
            if (Boolean.FALSE.equals(status) && Boolean.TRUE.equals(category.getStatus())) {
                long activeCount = categoryRepository.countActiveProductsByCategoryId(id, em);
                if (activeCount > 0) {
                    throw new IllegalStateException("Không thể vô hiệu hóa danh mục '" + category.getName() + "' vì còn " + activeCount + " sản phẩm đang hoạt động.");
                }
            }

            category.setName(name.trim());
            category.setDescription(description != null ? description.trim() : null);
            category.setStatus(status != null ? status : true);

            categoryRepository.update(category, em);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void toggleCategoryStatus(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category category = categoryRepository.findById(id, em)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục id: " + id));

            boolean currentStatus = Boolean.TRUE.equals(category.getStatus());
            boolean targetStatus = !currentStatus;

            // Ràng buộc: Disable chỉ được khi không còn product active
            if (!targetStatus) {
                long activeCount = categoryRepository.countActiveProductsByCategoryId(id, em);
                if (activeCount > 0) {
                    throw new IllegalStateException("Không thể tắt danh mục '" + category.getName() + "' vì đang có " + activeCount + " sản phẩm active thuộc danh mục này.");
                }
            }

            category.setStatus(targetStatus);
            categoryRepository.update(category, em);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
