package repository;

import dto.ProductSearchDTO;
import entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class ProductRepository {

    public void save(Product product) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (product.getId() == null) {
                em.persist(product);
            } else {
                em.merge(product);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<Product> findByIdWithDetail(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            // JOIN FETCH để giải quyết N+1 query
            String jpql = "SELECT p FROM Product p JOIN FETCH p.category LEFT JOIN FETCH p.detail WHERE p.id = :id AND p.deleted = false";
            List<Product> results = em.createQuery(jpql, Product.class)
                    .setParameter("id", id)
                    .getResultList();
            return results.stream().findFirst();
        }
    }

    public boolean existsBySku(String sku, Long excludeId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            String jpql = "SELECT COUNT(p) FROM Product p WHERE p.sku = :sku AND p.deleted = false";
            if (excludeId != null) {
                jpql += " AND p.id != :excludeId";
            }
            TypedQuery<Long> query = em.createQuery(jpql, Long.class).setParameter("sku", sku);
            if (excludeId != null) {
                query.setParameter("excludeId", excludeId);
            }
            return query.getSingleResult() > 0;
        }
    }

    public List<Product> search(ProductSearchDTO criteria) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            StringBuilder jpql = new StringBuilder("SELECT DISTINCT p FROM Product p JOIN FETCH p.category WHERE p.deleted = false");
            buildWhereClause(jpql, criteria);

            // Sorting
            String sortBy = criteria.getSortBy();
            if (!List.of("name", "price", "createdAt").contains(sortBy)) sortBy = "createdAt";
            String sortDir = "asc".equalsIgnoreCase(criteria.getSortDir()) ? "ASC" : "DESC";
            jpql.append(" ORDER BY p.").append(sortBy).append(" ").append(sortDir);

            TypedQuery<Product> query = em.createQuery(jpql.toString(), Product.class);
            setParameters(query, criteria);

            // Pagination
            query.setFirstResult((criteria.getPage() - 1) * criteria.getSize());
            query.setMaxResults(criteria.getSize());

            return query.getResultList();
        }
    }

    public long count(ProductSearchDTO criteria) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE p.deleted = false");
            buildWhereClause(jpql, criteria);

            TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
            setParameters(query, criteria);

            return query.getSingleResult();
        }
    }

    private void buildWhereClause(StringBuilder jpql, ProductSearchDTO criteria) {
        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            jpql.append(" AND (LOWER(p.name) LIKE :keyword OR LOWER(p.sku) LIKE :keyword)");
        }
        if (criteria.getCategoryId() != null) {
            jpql.append(" AND p.category.id = :categoryId");
        }
        if (criteria.getStatus() != null) {
            jpql.append(" AND p.status = :status");
        }
        if (criteria.getMinPrice() != null) {
            jpql.append(" AND p.price >= :minPrice");
        }
        if (criteria.getMaxPrice() != null) {
            jpql.append(" AND p.price <= :maxPrice");
        }
    }

    private void setParameters(TypedQuery<?> query, ProductSearchDTO criteria) {
        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            query.setParameter("keyword", "%" + criteria.getKeyword().trim().toLowerCase() + "%");
        }
        if (criteria.getCategoryId() != null) {
            query.setParameter("categoryId", criteria.getCategoryId());
        }
        if (criteria.getStatus() != null) {
            query.setParameter("status", criteria.getStatus());
        }
        if (criteria.getMinPrice() != null) {
            query.setParameter("minPrice", criteria.getMinPrice());
        }
        if (criteria.getMaxPrice() != null) {
            query.setParameter("maxPrice", criteria.getMaxPrice());
        }
    }
}