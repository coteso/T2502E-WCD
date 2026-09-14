package repository;

import dto.ProductSearchDTO;
import entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class ProductRepository {

    public List<Product> search(ProductSearchDTO criteria, EntityManager em) {
        StringBuilder jpql = new StringBuilder(
            "SELECT DISTINCT p FROM Product p JOIN FETCH p.category c LEFT JOIN FETCH p.detail d WHERE p.deleted = false"
        );
        applySearchConditions(jpql, criteria);
        applySorting(jpql, criteria);

        TypedQuery<Product> query = em.createQuery(jpql.toString(), Product.class);
        setSearchParameters(query, criteria);

        query.setFirstResult(criteria.getOffset());
        query.setMaxResults(criteria.getSize());

        return query.getResultList();
    }

    public long count(ProductSearchDTO criteria, EntityManager em) {
        StringBuilder jpql = new StringBuilder(
            "SELECT COUNT(p) FROM Product p WHERE p.deleted = false"
        );
        applySearchConditions(jpql, criteria);

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        setSearchParameters(query, criteria);

        return query.getSingleResult();
    }

    public Optional<Product> findByIdWithDetail(Long id, EntityManager em) {
        if (id == null) return Optional.empty();
        String jpql = "SELECT p FROM Product p JOIN FETCH p.category LEFT JOIN FETCH p.detail WHERE p.id = :id AND p.deleted = false";
        TypedQuery<Product> query = em.createQuery(jpql, Product.class);
        query.setParameter("id", id);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public boolean existsBySku(String sku, Long excludeId, EntityManager em) {
        if (sku == null || sku.trim().isEmpty()) return false;
        StringBuilder jpql = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE LOWER(p.sku) = LOWER(:sku) AND p.deleted = false");
        if (excludeId != null) {
            jpql.append(" AND p.id != :excludeId");
        }
        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        query.setParameter("sku", sku.trim());
        if (excludeId != null) {
            query.setParameter("excludeId", excludeId);
        }
        return query.getSingleResult() > 0;
    }

    public boolean existsBySku(String sku, EntityManager em) {
        return existsBySku(sku, null, em);
    }

    public Product save(Product product, EntityManager em) {
        em.persist(product);
        return product;
    }

    public Product update(Product product, EntityManager em) {
        return em.merge(product);
    }

    public void softDelete(Long id, EntityManager em) {
        findByIdWithDetail(id, em).ifPresent(p -> {
            p.setDeleted(true);
            em.merge(p);
        });
    }

    private void applySearchConditions(StringBuilder jpql, ProductSearchDTO criteria) {
        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            jpql.append(" AND (LOWER(p.name) LIKE LOWER(:keyword) OR LOWER(p.sku) LIKE LOWER(:keyword))");
        }
        if (criteria.getCategoryId() != null && criteria.getCategoryId() > 0) {
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

    private void setSearchParameters(TypedQuery<?> query, ProductSearchDTO criteria) {
        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            query.setParameter("keyword", "%" + criteria.getKeyword().trim() + "%");
        }
        if (criteria.getCategoryId() != null && criteria.getCategoryId() > 0) {
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

    private void applySorting(StringBuilder jpql, ProductSearchDTO criteria) {
        String sortBy = "p.id";
        if ("name".equalsIgnoreCase(criteria.getSortBy())) {
            sortBy = "p.name";
        } else if ("price".equalsIgnoreCase(criteria.getSortBy())) {
            sortBy = "p.price";
        } else if ("createdAt".equalsIgnoreCase(criteria.getSortBy())) {
            sortBy = "p.createdAt";
        } else if ("sku".equalsIgnoreCase(criteria.getSortBy())) {
            sortBy = "p.sku";
        }

        String sortDir = "ASC".equalsIgnoreCase(criteria.getSortDir()) ? "ASC" : "DESC";
        jpql.append(" ORDER BY ").append(sortBy).append(" ").append(sortDir);
    }
}
