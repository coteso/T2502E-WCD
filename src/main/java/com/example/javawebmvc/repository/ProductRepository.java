package com.example.javawebmvc.repository;

import com.example.javawebmvc.dto.PageResult;
import com.example.javawebmvc.dto.ProductSearchDTO;
import com.example.javawebmvc.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository cho Product - dùng Criteria API + JPQL, không nối chuỗi SQL tuỳ tiện.
 * Danh sách luôn join fetch Category (để tránh N+1) và LEFT JOIN fetch Detail.
 */
public class ProductRepository {

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "name", "name",
            "price", "price",
            "createdAt", "createdAt");

    /** Tìm kiếm + lọc + sắp xếp + phân trang (Criteria API động). */
    public PageResult<Product> search(EntityManager em, ProductSearchDTO c) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> p = cq.from(Product.class);
        p.fetch("category", JoinType.LEFT); // chống N+1 khi hiển thị tên danh mục

        List<Predicate> where = buildPredicates(cb, p, c);
        cq.where(where.toArray(new Predicate[0]));
        cq.orderBy(buildOrder(cb, p, c));
        cq.distinct(true); // fetch join có thể nhân bản dòng

        List<Product> items = em.createQuery(cq)
                .setFirstResult(c.getOffset())
                .setMaxResults(c.getSize())
                .getResultList();

        long total = count(em, c);
        return new PageResult<>(items, total, c.getPage(), c.getSize());
    }

    /** Đếm số dòng khớp điều kiện lọc (không fetch join để GROUP BY nhẹ hơn). */
    public long count(EntityManager em, ProductSearchDTO c) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Product> p = cq.from(Product.class);
        cq.select(cb.countDistinct(p));
        cq.where(buildPredicates(cb, p, c).toArray(new Predicate[0]));
        Long result = em.createQuery(cq).getSingleResult();
        return result != null ? result : 0;
    }

    /** Build WHERE động: keyword, category, status, khoảng giá. */
    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Product> p, ProductSearchDTO c) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isFalse(p.get("deleted"))); // luôn ẩn sản phẩm đã xoá mềm

        if (c.getKeyword() != null && !c.getKeyword().isBlank()) {
            String like = "%" + c.getKeyword().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(p.get("name")), like),
                    cb.like(cb.lower(p.get("sku")), like)));
        }

        if (c.getCategoryId() != null) {
            predicates.add(cb.equal(p.get("category").get("id"), c.getCategoryId()));
        }

        if ("active".equalsIgnoreCase(c.getStatus())) {
            predicates.add(cb.isTrue(p.get("status")));
        } else if ("inactive".equalsIgnoreCase(c.getStatus())) {
            predicates.add(cb.isFalse(p.get("status")));
        }

        if (c.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(p.get("price"), c.getMinPrice()));
        }
        if (c.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(p.get("price"), c.getMaxPrice()));
        }

        return predicates;
    }

    /** Sắp xếp name | price | createdAt với hướng asc | desc (whitelist, không nối chuỗi tuỳ tiện). */
    private List<Order> buildOrder(CriteriaBuilder cb, Root<Product> p, ProductSearchDTO c) {
        String field = SORT_FIELDS.getOrDefault(c.getSortBy(), "createdAt");
        Order order = "asc".equalsIgnoreCase(c.getSortDir())
                ? cb.asc(p.get(field))
                : cb.desc(p.get(field));
        // secondary sort theo id để phân trang ổn định
        return List.of(order, cb.asc(p.get("id")));
    }

    /** Load Product kèm Detail + Category bằng một query (tránh lazy exception). */
    public Optional<Product> findByIdWithDetail(EntityManager em, Long id) {
        List<Product> result = em.createQuery(
                        "SELECT p FROM Product p "
                                + "LEFT JOIN FETCH p.detail "
                                + "JOIN FETCH p.category "
                                + "WHERE p.id = :id AND p.deleted = false",
                        Product.class)
                .setParameter("id", id)
                .getResultList();
        return result.stream().findFirst();
    }

    public boolean existsBySku(EntityManager em, String sku) {
        Long count = em.createQuery(
                        "SELECT COUNT(p.id) FROM Product p WHERE LOWER(p.sku) = LOWER(:sku)",
                        Long.class)
                .setParameter("sku", sku)
                .getSingleResult();
        return count != null && count > 0;
    }

    /** SKU trùng nhưng thuộc product khác (dùng khi UPDATE). */
    public boolean existsBySkuAndIdNot(EntityManager em, String sku, Long excludeId) {
        Long count = em.createQuery(
                        "SELECT COUNT(p.id) FROM Product p WHERE LOWER(p.sku) = LOWER(:sku) AND p.id <> :id",
                        Long.class)
                .setParameter("sku", sku)
                .setParameter("id", excludeId)
                .getSingleResult();
        return count != null && count > 0;
    }

    public Optional<Product> findById(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Product.class, id));
    }

    public Product save(EntityManager em, Product product) {
        if (product.getId() == null) {
            em.persist(product);
            return product;
        }
        return em.merge(product);
    }

    /** Soft delete: chỉ set deleted = true, không DELETE vật lý. */
    public void softDelete(EntityManager em, Long id) {
        em.createQuery("UPDATE Product p SET p.deleted = true, p.updatedAt = CURRENT_TIMESTAMP "
                        + "WHERE p.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    /** Đếm nhanh số product chưa xoá (dùng cho dashboard/thống kê nếu cần). */
    public long countAll(EntityManager em) {
        Long count = em.createQuery("SELECT COUNT(p.id) FROM Product p WHERE p.deleted = false", Long.class)
                .getSingleResult();
        return count != null ? count : 0;
    }

    /** Giữ chữ ký TypedQuery được dùng ít nhất 1 lần (yêu cầu đề bài). */
    public List<Product> findByCategory(EntityManager em, Long categoryId) {
        TypedQuery<Product> q = em.createQuery(
                "SELECT p FROM Product p JOIN FETCH p.category "
                        + "WHERE p.category.id = :categoryId AND p.deleted = false "
                        + "ORDER BY p.createdAt DESC",
                Product.class);
        q.setParameter("categoryId", categoryId);
        return q.getResultList();
    }

    /** NoResultException được xử lý đúng ở nơi gọi - giữ mẫu này làm ví dụ tham khảo. */
    public Optional<Product> findBySku(EntityManager em, String sku) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT p FROM Product p JOIN FETCH p.category WHERE p.sku = :sku",
                            Product.class)
                    .setParameter("sku", sku)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
