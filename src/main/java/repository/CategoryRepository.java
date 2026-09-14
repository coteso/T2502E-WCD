package repository;

import entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class CategoryRepository {

    public List<Category> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
        }
    }

    public Optional<Category> findById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return Optional.ofNullable(em.find(Category.class, id));
        }
    }

    public void save(Category category) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (category.getId() == null) {
                em.persist(category);
            } else {
                em.merge(category);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public long countActiveProductsByCategoryId(Long categoryId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            String jpql = "SELECT COUNT(p) FROM Product p WHERE p.category.id = :catId AND p.deleted = false AND p.status = true";
            return em.createQuery(jpql, Long.class)
                    .setParameter("catId", categoryId)
                    .getSingleResult();
        }
    }
}