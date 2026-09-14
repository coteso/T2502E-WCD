package repository;

import entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class CategoryRepository {

    public Category save(Category category, EntityManager em) {
        em.persist(category);
        return category;
    }

    public Category update(Category category, EntityManager em) {
        return em.merge(category);
    }

    public Optional<Category> findById(Long id, EntityManager em) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(em.find(Category.class, id));
    }

    public List<Category> findAll(EntityManager em) {
        TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c ORDER BY c.name ASC", Category.class);
        return query.getResultList();
    }

    public boolean existsByName(String name, Long excludeId, EntityManager em) {
        if (name == null || name.trim().isEmpty()) return false;
        StringBuilder jpql = new StringBuilder("SELECT COUNT(c) FROM Category c WHERE LOWER(c.name) = LOWER(:name)");
        if (excludeId != null) {
            jpql.append(" AND c.id != :excludeId");
        }
        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        query.setParameter("name", name.trim());
        if (excludeId != null) {
            query.setParameter("excludeId", excludeId);
        }
        return query.getSingleResult() > 0;
    }

    public long countActiveProductsByCategoryId(Long categoryId, EntityManager em) {
        String jpql = "SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.deleted = false AND p.status = true";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("categoryId", categoryId);
        return query.getSingleResult();
    }
}
