package service;

import entity.Category;
import repository.CategoryRepository;
import repository.ProductRepository;
import jakarta.persistence.EntityManager;
import util.JPAUtil;
import java.util.List;

public class CategoryService {
    public List<Category> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new CategoryRepository(em).findAll();
        } finally {
            em.close();
        }
    }

    public void disableCategory(Long id) throws Exception {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            CategoryRepository catRepo = new CategoryRepository(em);
            ProductRepository prodRepo = new ProductRepository(em);

            if (prodRepo.hasActiveProducts(id)) {
                throw new Exception("Không thể vô hiệu hoá danh mục vì vẫn còn sản phẩm đang kích hoạt.");
            }

            Category category = catRepo.findById(id)
                    .orElseThrow(() -> new Exception("Không tìm thấy danh mục."));
            category.setStatus(false);
            em.merge(category);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
