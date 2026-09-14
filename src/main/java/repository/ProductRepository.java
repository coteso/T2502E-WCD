package repository;

import dto.ProductSearchDTO;
import entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductRepository {
    private final EntityManager em;

    public ProductRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Product product) {
        if (product.getId() == null) {
            em.persist(product);
        } else {
            em.merge(product);
        }
    }

    public boolean existsBySku(String sku) {
        Long count = em.createQuery("SELECT COUNT(p) FROM Product p WHERE p.sku = :sku", Long.class)
                .setParameter("sku", sku)
                .getSingleResult();
        return count > 0;
    }

    public boolean existsBySkuAndIdNot(String sku, Long id) {
        Long count = em.createQuery("SELECT COUNT(p) FROM Product p WHERE p.sku = :sku AND p.id != :id", Long.class)
                .setParameter("sku", sku)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }

    public Optional<Product> findByIdWithDetail(Long id) {
        List<Product> list = em.createQuery(
                "SELECT p FROM Product p LEFT JOIN FETCH p.detail LEFT JOIN FETCH p.category WHERE p.id = :id AND p.deleted = false",
                Product.class
        ).setParameter("id", id).getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<Product> search(ProductSearchDTO criteria) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM Product p JOIN FETCH p.category WHERE p.deleted = false ");
        Map<String, Object> params = buildConditions(criteria, jpql);

        String sortCol = "p.createdAt";
        if ("price".equalsIgnoreCase(criteria.getSortBy())) sortCol = "p.price";
        else if ("name".equalsIgnoreCase(criteria.getSortBy())) sortCol = "p.name";

        String sortDirection = "desc".equalsIgnoreCase(criteria.getSortDir()) ? "DESC" : "ASC";
        jpql.append(" ORDER BY ").append(sortCol).append(" ").append(sortDirection);

        TypedQuery<Product> query = em.createQuery(jpql.toString(), Product.class);
        params.forEach(query::setParameter);

        int offset = (criteria.getPage() - 1) * criteria.getSize();
        query.setFirstResult(offset);
        query.setMaxResults(criteria.getSize());

        return query.getResultList();
    }

    public long count(ProductSearchDTO criteria) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE p.deleted = false ");
        Map<String, Object> params = buildConditions(criteria, jpql);

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        params.forEach(query::setParameter);

        return query.getSingleResult();
    }

    private Map<String, Object> buildConditions(ProductSearchDTO criteria, StringBuilder jpql) {
        Map<String, Object> params = new HashMap<>();

        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            jpql.append(" AND (LOWER(p.name) LIKE :kw OR LOWER(p.sku) LIKE :kw)");
            params.put("kw", "%" + criteria.getKeyword().trim().toLowerCase() + "%");
        }
        if (criteria.getCategoryId() != null && criteria.getCategoryId() > 0) {
            jpql.append(" AND p.category.id = :catId");
            params.put("catId", criteria.getCategoryId());
        }
        if (criteria.getStatus() != null) {
            jpql.append(" AND p.status = :status");
            params.put("status", criteria.getStatus());
        }
        if (criteria.getMinPrice() != null) {
            jpql.append(" AND p.price >= :minPrice");
            params.put("minPrice", criteria.getMinPrice());
        }
        if (criteria.getMaxPrice() != null) {
            jpql.append(" AND p.price <= :maxPrice");
            params.put("maxPrice", criteria.getMaxPrice());
        }
        return params;
    }
}