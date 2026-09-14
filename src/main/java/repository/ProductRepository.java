package repository;

import dto.ProductSearchDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import entity.Category;
import entity.Product;
import entity.ProductDetail;
import util.JPAUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ProductRepository - Lớp truy cập dữ liệu (Data Access Layer)
 * 
 * Repository Pattern:
 * - Tách logic truy vấn database ra khỏi Business Logic
 * - Chỉ chứa các phương thức CRUD (Create, Read, Update, Delete)
 * - Không chứa logic Business
 * 
 * Cách hoạt động:
 * 1. Service gọi Repository methods
 * 2. Repository sử dụng EntityManager để truy vấn JPA
 * 3. Trả kết quả về cho Service
 */
public class ProductRepository {

    /**
     * Tìm sản phẩm theo ID kèm theo chi tiết sản phẩm (ProductDetail)
     * 
     * Tại sao fetch ProductDetail?
     * - Khi load Product, theo mặc định ProductDetail không được load (LAZY fetch)
     * - Dùng JOIN FETCH để load cùng lúc, tránh N+1 query problem
     * 
     * @param id: ID sản phẩm
     * @return: Optional<Product> (có hoặc không)
     */
    public Optional<Product> findByIdWithDetail(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // JPQL query với JOIN FETCH để load cùng lúc Product + ProductDetail
            String jpql = "SELECT p FROM Product p " +
                         "LEFT JOIN FETCH p.detail " +  // LEFT JOIN: lấy Product dù không có Detail
                         "WHERE p.id = :id AND p.deleted = false";
            
            TypedQuery<Product> query = em.createQuery(jpql, Product.class);
            query.setParameter("id", id);
            
            // getSingleResult() trả về 1 entity hoặc ném NoResultException
            // Dùng Optional để xử lý an toàn
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            System.err.println("❌ Lỗi tìm Product by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Tìm sản phẩm theo SKU (kiểm tra trùng lặp)
     * 
     * @param sku: Mã SKU
     * @return: true nếu SKU đã tồn tại
     */
    public boolean existsBySku(String sku) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(p) FROM Product p WHERE p.sku = :sku AND p.deleted = false";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("sku", sku);
            
            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            System.err.println("❌ Lỗi kiểm tra SKU: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Tìm kiếm, lọc, sắp xếp và phân trang sản phẩm
     * 
     * Tiêu chí tìm kiếm từ ProductSearchDTO:
     * - keyword: tìm theo name hoặc sku
     * - categoryId: lọc theo danh mục
     * - status: lọc theo trạng thái
     * - minPrice, maxPrice: lọc theo khoảng giá
     * - sortBy, sortDir: sắp xếp
     * - page, size: phân trang
     * 
     * @param criteria: ProductSearchDTO chứa tiêu chí tìm kiếm
     * @return: Danh sách sản phẩm
     */
    public List<Product> search(ProductSearchDTO criteria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Xây dựng JPQL query động
            String jpql = buildSearchQuery(criteria);
            
            TypedQuery<Product> query = em.createQuery(jpql, Product.class);
            
            // Set các tham số vào query
            setSearchParameters(query, criteria);
            
            // Thiết lập phân trang
            // setFirstResult: bỏ qua số record đầu tiên (offset)
            // setMaxResults: giới hạn số record trả về
            query.setFirstResult(criteria.getOffset());
            query.setMaxResults(criteria.getSize());
            
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("❌ Lỗi tìm kiếm Product: " + e.getMessage());
            return new ArrayList<>();  // Java 8 compatible
        } finally {
            em.close();
        }
    }

    /**
     * Đếm số lượng sản phẩm khớp với tiêu chí tìm kiếm (dùng cho phân trang)
     * 
     * @param criteria: ProductSearchDTO chứa tiêu chí tìm kiếm
     * @return: Tổng số sản phẩm
     */
    public long count(ProductSearchDTO criteria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = buildCountQuery(criteria);
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            setSearchParameters(query, criteria);
            
            return query.getSingleResult();
        } catch (Exception e) {
            System.err.println("❌ Lỗi đếm Product: " + e.getMessage());
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Xây dựng JPQL query cho tìm kiếm
     */
    private String buildSearchQuery(ProductSearchDTO criteria) {
        StringBuilder jpql = new StringBuilder(
            "SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +  // LEFT JOIN FETCH để load Category cùng lúc
            "WHERE p.deleted = false"
        );

        // Thêm điều kiện keyword (tìm trong name hoặc sku)
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            jpql.append(" AND (LOWER(p.name) LIKE LOWER(:keyword) OR LOWER(p.sku) LIKE LOWER(:keyword))");
        }

        // Thêm điều kiện category
        if (criteria.getCategoryId() != null) {
            jpql.append(" AND p.category.id = :categoryId");
        }

        // Thêm điều kiện status
        if (criteria.getStatus() != null) {
            jpql.append(" AND p.status = :status");
        }

        // Thêm điều kiện khoảng giá
        if (criteria.getMinPrice() != null) {
            jpql.append(" AND p.price >= :minPrice");
        }
        if (criteria.getMaxPrice() != null) {
            jpql.append(" AND p.price <= :maxPrice");
        }

        // Thêm ORDER BY (sắp xếp)
        jpql.append(" ORDER BY ");
        
        // Xác định trường sắp xếp
        String sortBy = criteria.getSortBy();
        if ("price".equals(sortBy)) {
            jpql.append("p.price");
        } else if ("name".equals(sortBy)) {
            jpql.append("p.name");
        } else {
            jpql.append("p.createdAt"); // Mặc định sắp xếp theo ngày tạo
        }
        
        // Xác định chiều sắp xếp
        if ("asc".equalsIgnoreCase(criteria.getSortDir())) {
            jpql.append(" ASC");
        } else {
            jpql.append(" DESC");
        }

        return jpql.toString();
    }

    /**
     * Xây dựng JPQL query cho đếm
     */
    private String buildCountQuery(ProductSearchDTO criteria) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE p.deleted = false");

        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            jpql.append(" AND (LOWER(p.name) LIKE LOWER(:keyword) OR LOWER(p.sku) LIKE LOWER(:keyword))");
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

        return jpql.toString();
    }

    /**
     * Set các tham số vào TypedQuery
     */
    private <T> void setSearchParameters(TypedQuery<T> query, ProductSearchDTO criteria) {
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            query.setParameter("keyword", "%" + criteria.getKeyword() + "%");
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

    /**
     * Lưu sản phẩm mới vào database
     * 
     * Transaction được quản lý trong Service, không trong Repository
     * 
     * @param product: Sản phẩm cần lưu
     * @return: Sản phẩm đã được lưu (có ID được sinh tự động)
     */
    public Product save(Product product) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // persist: Chuyển entity thành trạng thái managed (được Hibernate theo dõi)
            em.persist(product);
            
            em.getTransaction().commit();
            return product;
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("❌ Lỗi lưu Product: " + e.getMessage());
            throw new RuntimeException("Lỗi lưu sản phẩm: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật sản phẩm
     * 
     * @param product: Sản phẩm cần cập nhật
     * @return: Sản phẩm đã cập nhật
     */
    public Product update(Product product) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // merge: Cập nhật entity (lấy version từ database rồi cập nhật)
            Product merged = em.merge(product);
            
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("❌ Lỗi cập nhật Product: " + e.getMessage());
            throw new RuntimeException("Lỗi cập nhật sản phẩm: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Xóa mềm sản phẩm (set deleted = true)
     * 
     * Không xóa vật lý khỏi database, chỉ đánh dấu deleted=true
     * Điều này giúp giữ lại dữ liệu lịch sử
     * 
     * @param id: ID sản phẩm cần xóa
     */
    public void softDelete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            Product product = em.find(Product.class, id);
            if (product != null) {
                product.setDeleted(true);
                em.merge(product);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("❌ Lỗi xóa mềm Product: " + e.getMessage());
            throw new RuntimeException("Lỗi xóa sản phẩm: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}
