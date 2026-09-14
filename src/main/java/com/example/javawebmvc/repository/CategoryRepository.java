package com.example.javawebmvc.repository;

import com.example.javawebmvc.entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Category - mọi truy vấn đi qua JPQL/Criteria, không nối chuỗi SQL tuỳ tiện.
 */
public class CategoryRepository {

    public List<Category> findAll(EntityManager em) {
        return em.createQuery(
                        "SELECT c FROM Category c ORDER BY c.name", Category.class)
                .getResultList();
    }

    public Optional<Category> findById(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Category.class, id));
    }

    public boolean existsByName(EntityManager em, String name) {
        Long count = em.createQuery(
                        "SELECT COUNT(c.id) FROM Category c WHERE LOWER(c.name) = LOWER(:name)", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean existsByNameAndIdNot(EntityManager em, String name, Long excludeId) {
        Long count = em.createQuery(
                        "SELECT COUNT(c.id) FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND c.id <> :id",
                        Long.class)
                .setParameter("name", name)
                .setParameter("id", excludeId)
                .getSingleResult();
        return count != null && count > 0;
    }

    /** Số Product CHƯA xoá mềm thuộc category - dùng để chặn delete/disable. */
    public long countProducts(EntityManager em, Long categoryId, boolean onlyActive) {
        Long count = em.createQuery(
                        "SELECT COUNT(p.id) FROM Product p "
                                + "WHERE p.category.id = :categoryId AND p.deleted = false "
                                + (onlyActive ? "AND p.status = true" : ""),
                        Long.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();
        return count != null ? count : 0;
    }

    /**
     * Đếm category kèm số product chưa xoá (total) và số product active
     * trong MỘT query duy nhất (LEFT JOIN + SUM CASE) - chống N+1 khi vẽ bảng.
     * Trả về Object[] { Category, Long total, Long active }.
     */
    public List<Object[]> findAllWithCounts(EntityManager em) {
        return em.createQuery(
                        "SELECT c, " +
                                "SUM(CASE WHEN p.deleted = false THEN 1 ELSE 0 END), " +
                                "SUM(CASE WHEN p.deleted = false AND p.status = true THEN 1 ELSE 0 END) " +
                                "FROM Category c LEFT JOIN c.products p " +
                                "GROUP BY c " +
                                "ORDER BY c.name",
                        Object[].class)
                .getResultList();
    }

    public Category save(EntityManager em, Category category) {
        if (category.getId() == null) {
            em.persist(category);
            return category;
        }
        return em.merge(category);
    }

    /** Tìm theo tên (không phân biệt hoa thường) - dùng khi kiểm tra trùng tên. */
    public Optional<Category> findByName(EntityManager em, String name) {
        try {
            TypedQuery<Category> q = em.createQuery(
                    "SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name)", Category.class);
            q.setParameter("name", name);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
