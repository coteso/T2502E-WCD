package repository;

import entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class CategoryRepository {
    private final EntityManager em;

    public CategoryRepository(EntityManager em) {
        this.em = em;
    }

    public List<Category> findAll() {
        return em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }

    public List<Category> findActiveCategories() {
        return em.createQuery("SELECT c FROM Category c WHERE c.status = true", Category.class).getResultList();
    }

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(em.find(Category.class, id));
    }

    public void save(Category category) {
        if (category.getId() == null) {
            em.persist(category);
        } else {
            em.merge(category);
        }
    }

    // Kiểm tra xem danh mục còn sản phẩm active nào không (để cấm disable)
    public boolean hasActiveProducts(Long categoryId) {
        Long count = em.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.category.id = :catId AND p.status = true AND p.deleted = false",
                Long.class
        ).setParameter("catId", categoryId).getSingleResult();
        return count > 0;
    }
}