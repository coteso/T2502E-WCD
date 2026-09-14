package repository;

import dto.ProductSearchDTO;
import entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {
    private EntityManager em;

    public ProductRepository(EntityManager em) {
        this.em = em;
    }

    public boolean existsBySku(String sku) {
        Long count = em.createQuery("SELECT COUNT(p) FROM Product p WHERE p.sku = :sku AND p.deleted = false", Long.class)
                .setParameter("sku", sku)
                .getSingleResult();
        return count > 0;
    }

    public Optional<Product> findByIdWithDetail(Long id) {
        List<Product> results = em.createQuery(
                        "SELECT p FROM Product p LEFT JOIN FETCH p.detail LEFT JOIN FETCH p.category WHERE p.id = :id AND p.deleted = false", Product.class)
                .setParameter("id", id)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public boolean hasActiveProducts(Long categoryId) {
        Long count = em.createQuery("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.deleted = false AND p.status = true", Long.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();
        return count > 0;
    }

    public List<Product> search(ProductSearchDTO criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        root.fetch("category", JoinType.LEFT);

        List<Predicate> predicates = buildPredicates(cb, root, criteria);
        cq.where(predicates.toArray(new Predicate[0]));

        if ("asc".equalsIgnoreCase(criteria.getSortDir())) {
            cq.orderBy(cb.asc(root.get(criteria.getSortBy())));
        } else {
            cq.orderBy(cb.desc(root.get(criteria.getSortBy())));
        }

        TypedQuery<Product> query = em.createQuery(cq);
        query.setFirstResult((criteria.getPage() - 1) * criteria.getSize());
        query.setMaxResults(criteria.getSize());
        return query.getResultList();
    }

    public long count(ProductSearchDTO criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Product> root = cq.from(Product.class);

        List<Predicate> predicates = buildPredicates(cb, root, criteria);
        cq.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getSingleResult();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Product> root, ProductSearchDTO criteria) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deleted"), false));

        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            String pattern = "%" + criteria.getKeyword().toLowerCase() + "%";
            Predicate nameMatch = cb.like(cb.lower(root.get("name")), pattern);
            Predicate skuMatch = cb.like(cb.lower(root.get("sku")), pattern);
            predicates.add(cb.or(nameMatch, skuMatch));
        }
        if (criteria.getCategoryId() != null && criteria.getCategoryId() > 0) {
            predicates.add(cb.equal(root.get("category").get("id"), criteria.getCategoryId()));
        }
        if (criteria.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
        }
        if (criteria.getMinPrice() != null) {
            predicates.add(cb.ge(root.get("price"), criteria.getMinPrice()));
        }
        if (criteria.getMaxPrice() != null) {
            predicates.add(cb.le(root.get("price"), criteria.getMaxPrice()));
        }
        return predicates;
    }
}