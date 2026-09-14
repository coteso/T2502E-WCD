package service;

import dto.ProductFormDTO;
import dto.ProductSearchDTO;
import entity.Category;
import entity.Product;
import entity.ProductDetail;
import repository.CategoryRepository;
import repository.ProductRepository;
import util.JPAUtil;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ProductService - Lớp Business Logic cho sản phẩm
 * 
 * Service Layer:
 * - Chứa tất cả logic business (validate, tính toán, quyết định)
 * - Gọi Repository để truy cập database
 * - Không chứa logic Servlet (HTTP handling)
 * - Quản lý Transaction
 * 
 * Lợi ích:
 * - Code dễ test (có thể mock Repository)
 * - Tách biệt logic Business khỏi HTTP handling
 * - Tái sử dụng được trong các context khác (API, batch job, v.v.)
 */
public class ProductService {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    public ProductService() {
        this.productRepository = new ProductRepository();
        this.categoryRepository = new CategoryRepository();
    }

    /**
     * Tạo sản phẩm mới cùng với chi tiết sản phẩm
     * 
     * Quy trình:
     * 1. Validate dữ liệu (tên, giá, SKU, category, v.v.)
     * 2. Kiểm tra SKU có trùng lặp không
     * 3. Kiểm tra Category có tồn tại không
     * 4. Tạo Product + ProductDetail trong 1 transaction (atomicity)
     * 5. Nếu lỗi → rollback (không lưu cả 2)
     * 
     * @param formDTO: Dữ liệu form từ JSP
     * @return: Sản phẩm vừa tạo
     * @throws: RuntimeException nếu có lỗi validate hoặc database
     */
    public Product createProduct(ProductFormDTO formDTO) {
        // 1️⃣ Validate dữ liệu
        validateProductForm(formDTO);

        // 2️⃣ Kiểm tra SKU trùng lặp
        if (productRepository.existsBySku(formDTO.getSku())) {
            throw new RuntimeException("❌ SKU '" + formDTO.getSku() + "' đã tồn tại!");
        }

        // 3️⃣ Kiểm tra Category tồn tại
        Optional<Category> categoryOpt = categoryRepository.findById(formDTO.getCategoryId());
        if (!categoryOpt.isPresent()) {  // Java 8 compatible
            throw new RuntimeException("❌ Danh mục không tồn tại!");
        }

        Category category = categoryOpt.get();
        if (!category.getStatus()) {
            throw new RuntimeException("❌ Danh mục không hoạt động!");
        }

        // 4️⃣ Tạo Product (và ProductDetail trong cùng 1 transaction)
        Product product = new Product(
            formDTO.getSku(),
            formDTO.getName(),
            formDTO.getPrice(),
            formDTO.getQuantity(),
            formDTO.getDescription(),
            category,
            formDTO.getStatus() != null ? formDTO.getStatus() : true
        );

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // persist product (managed)
            em.persist(product);

            // Tạo detail và liên kết
            ProductDetail detail = new ProductDetail(
                formDTO.getManufacturer(),
                formDTO.getWarrantyMonths(),
                formDTO.getOrigin(),
                formDTO.getDetailDescription(),
                formDTO.getTechnicalSpec(),
                product
            );

            // Persist detail (owner side)
            em.persist(detail);

            // Thiết lập quan hệ 2 chiều (product đã managed)
            product.setDetail(detail);

            em.getTransaction().commit();
            return product;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Lỗi tạo sản phẩm: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật sản phẩm hiện có
     * 
     * Quy trình tương tự tạo mới, nhưng:
     * - Không cần kiểm tra SKU trùng nếu SKU không đổi
     * - Cập nhật ProductDetail nếu có
     * 
     * @param formDTO: Dữ liệu form với productId
     * @return: Sản phẩm đã cập nhật
     */
    public Product updateProduct(ProductFormDTO formDTO) {
        // 1️⃣ Validate
        validateProductForm(formDTO);

        // 2️⃣ Thực hiện update trong 1 transaction để đồng bộ Product và Detail
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Product product = em.find(Product.class, formDTO.getProductId());
            if (product == null) {
                throw new RuntimeException("❌ Sản phẩm không tồn tại!");
            }

            // 3️⃣ Kiểm tra SKU trùng (nếu SKU thay đổi)
            if (!product.getSku().equals(formDTO.getSku())) {
                // kiểm tra tồn tại SKU khác
                String jpql = "SELECT COUNT(p) FROM Product p WHERE p.sku = :sku AND p.id <> :id AND p.deleted = false";
                Long count = em.createQuery(jpql, Long.class)
                               .setParameter("sku", formDTO.getSku())
                               .setParameter("id", formDTO.getProductId())
                               .getSingleResult();
                if (count > 0) {
                    throw new RuntimeException("❌ SKU '" + formDTO.getSku() + "' đã tồn tại!");
                }
            }

            // 4️⃣ Kiểm tra Category
            if (product.getCategory() == null || !product.getCategory().getId().equals(formDTO.getCategoryId())) {
                Category category = em.find(Category.class, formDTO.getCategoryId());
                if (category == null) {
                    throw new RuntimeException("❌ Danh mục không tồn tại!");
                }
                product.setCategory(category);
            }

            // 5️⃣ Cập nhật thông tin Product
            product.setSku(formDTO.getSku());
            product.setName(formDTO.getName());
            product.setPrice(formDTO.getPrice());
            product.setQuantity(formDTO.getQuantity());
            product.setDescription(formDTO.getDescription());
            product.setStatus(formDTO.getStatus());
            product.setUpdatedAt(LocalDateTime.now());

            // 6️⃣ Cập nhật hoặc tạo ProductDetail
            if (product.getDetail() != null) {
                ProductDetail detail = product.getDetail();
                detail.setManufacturer(formDTO.getManufacturer());
                detail.setWarrantyMonths(formDTO.getWarrantyMonths());
                detail.setOrigin(formDTO.getOrigin());
                detail.setDescription(formDTO.getDetailDescription());
                detail.setTechnicalSpec(formDTO.getTechnicalSpec());
                em.merge(detail);
            } else {
                ProductDetail detail = new ProductDetail(
                    formDTO.getManufacturer(),
                    formDTO.getWarrantyMonths(),
                    formDTO.getOrigin(),
                    formDTO.getDetailDescription(),
                    formDTO.getTechnicalSpec(),
                    product
                );
                em.persist(detail);
                product.setDetail(detail);
            }

            em.getTransaction().commit();
            return product;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Lỗi cập nhật sản phẩm: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Xóa mềm sản phẩm
     * 
     * @param id: ID sản phẩm
     */
    public void deleteProduct(Long id) {
        Optional<Product> productOpt = productRepository.findByIdWithDetail(id);
        if (!productOpt.isPresent()) {  // Java 8 compatible
            throw new RuntimeException("❌ Sản phẩm không tồn tại!");
        }

        // Xóa mềm: chỉ set deleted = true
        productRepository.softDelete(id);
    }

    /**
     * Lấy sản phẩm theo ID kèm chi tiết
     */
    public Optional<Product> getProductWithDetail(Long id) {
        return productRepository.findByIdWithDetail(id);
    }

    /**
     * Tìm kiếm, lọc, phân trang sản phẩm
     */
    public List<Product> searchProducts(ProductSearchDTO criteria) {
        return productRepository.search(criteria);
    }

    /**
     * Đếm tổng sản phẩm khớp tiêu chí tìm kiếm
     */
    public long countProducts(ProductSearchDTO criteria) {
        return productRepository.count(criteria);
    }

    /**
     * Validate dữ liệu form sản phẩm
     * 
     * Các quy tắc validate (từ yêu cầu đề bài):
     * - Tên không rỗng
     * - Giá > 0
     * - Tồn kho >= 0
     * - Category bắt buộc
     * - Bảo hành >= 0
     * - Nhà sản xuất không rỗng
     * - Xuất xứ không rỗng
     * 
     * @param formDTO: Dữ liệu form
     * @throws: RuntimeException nếu validate thất bại
     */
    private void validateProductForm(ProductFormDTO formDTO) {
        // Kiểm tra SKU
        if (formDTO.getSku() == null || formDTO.getSku().trim().isEmpty()) {
            throw new RuntimeException("❌ SKU không được rỗng!");
        }

        // Kiểm tra Tên sản phẩm
        if (formDTO.getName() == null || formDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("❌ Tên sản phẩm không được rỗng!");
        }

        // Kiểm tra Giá
        if (formDTO.getPrice() == null || formDTO.getPrice().signum() <= 0) {
            throw new RuntimeException("❌ Giá sản phẩm phải > 0!");
        }

        // Kiểm tra Tồn kho
        if (formDTO.getQuantity() == null || formDTO.getQuantity() < 0) {
            throw new RuntimeException("❌ Tồn kho phải >= 0!");
        }

        // Kiểm tra Category
        if (formDTO.getCategoryId() == null || formDTO.getCategoryId() <= 0) {
            throw new RuntimeException("❌ Phải chọn danh mục!");
        }

        // Kiểm tra Nhà sản xuất
        if (formDTO.getManufacturer() == null || formDTO.getManufacturer().trim().isEmpty()) {
            throw new RuntimeException("❌ Nhà sản xuất không được rỗng!");
        }

        // Kiểm tra Bảo hành
        if (formDTO.getWarrantyMonths() == null || formDTO.getWarrantyMonths() < 0) {
            throw new RuntimeException("❌ Bảo hành phải >= 0!");
        }

        // Kiểm tra Xuất xứ
        if (formDTO.getOrigin() == null || formDTO.getOrigin().trim().isEmpty()) {
            throw new RuntimeException("❌ Xuất xứ không được rỗng!");
        }
    }
}
