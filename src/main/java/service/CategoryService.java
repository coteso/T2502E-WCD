package service;

import entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import repository.CategoryRepository;
import util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class CategoryService {

    public List<Category> getAllCategories() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new CategoryRepository(em).findAll();
        } finally {
            em.close();
        }
    }

    public List<Category> getActiveCategories() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new CategoryRepository(em).findActiveCategories();
        } finally {
            em.close();
        }
    }

    public Optional<Category> getCategoryById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new CategoryRepository(em).findById(id);
        } finally {
            em.close();
        }
    }

    public void saveOrUpdate(Category category) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            CategoryRepository repo = new CategoryRepository(em);

            // Kiểm tra ràng buộc disable danh mục khi còn sản phẩm active
            if (category.getId() != null && Boolean.FALSE.equals(category.getStatus())) {
                if (repo.hasActiveProducts(category.getId())) {
                    throw new Exception("Không thể vô hiệu hóa danh mục vì vẫn còn sản phẩm đang hoạt động!");
                }
            }

            repo.save(category);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}