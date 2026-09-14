package com.example.javawebmvc.service;

import com.example.javawebmvc.dto.PageResult;
import com.example.javawebmvc.dto.ProductFormDTO;
import com.example.javawebmvc.dto.ProductSearchDTO;
import com.example.javawebmvc.entity.Category;
import com.example.javawebmvc.entity.Product;
import com.example.javawebmvc.entity.ProductDetail;
import com.example.javawebmvc.exception.BusinessException;
import com.example.javawebmvc.exception.ValidationException;
import com.example.javawebmvc.repository.CategoryRepository;
import com.example.javawebmvc.repository.ProductRepository;
import com.example.javawebmvc.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service cho Product - toàn bộ nghiệp vụ + transaction nằm ở đây.
 *
 * Quy tắc transaction (mục 7 của đề):
 * - Create: persist Product + ProductDetail trong MỘT transaction; Detail lỗi => rollback cả Product.
 * - Update: đồng bộ Product + Category + Detail; SKU trùng => rollback.
 * - Soft delete: chỉ set deleted = true, không đụng Category và Detail.
 */
public class ProductService {

    private final ProductRepository productRepository = new ProductRepository();
    private final CategoryRepository categoryRepository = new CategoryRepository();

    // ------------------------------------------------------------------
    // SEARCH (chỉ đọc)
    // ------------------------------------------------------------------
    public PageResult<Product> search(ProductSearchDTO criteria) {
        criteria.normalize();
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return productRepository.search(em, criteria);
        } finally {
            em.close();
        }
    }

    // ------------------------------------------------------------------
    // READ 1 Product kèm Detail (join fetch, 1 query)
    // ------------------------------------------------------------------
    public Optional<Product> getWithDetail(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return productRepository.findByIdWithDetail(em, id);
        } finally {
            em.close();
        }
    }

    // ------------------------------------------------------------------
    // CREATE Product + Detail (1 transaction)
    // ------------------------------------------------------------------
    public Product create(ProductFormDTO dto) {
        Map<String, String> errors = validate(dto);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // Mọi thao tác ghi chạy trong transaction helper - rollback tự động khi ném exception.
        return JPAUtil.runInTransactionReturn(em -> {
            // SKU unique: nếu form để trống thì tự sinh, nếu nhập thì kiểm tra trùng.
            String sku = (dto.getSku() == null || dto.getSku().isBlank())
                    ? generateSku()
                    : dto.getSku();
            if (productRepository.existsBySku(em, sku)) {
                errors.put("sku", "SKU '" + sku + "' đã tồn tại, vui lòng dùng SKU khác.");
                throw new ValidationException(errors); // ném => tx rollback, Product chưa kịp lưu
            }

            // Category bắt buộc và phải đang active.
            Category category = categoryRepository.findById(em, dto.getCategoryId())
                    .orElseThrow(() -> new BusinessException("Danh mục không tồn tại (id=" + dto.getCategoryId() + ")."));
            if (!category.isStatus()) {
                errors.put("categoryId", "Danh mục '" + category.getName() + "' đang bị vô hiệu hoá, không thể gán sản phẩm.");
                throw new ValidationException(errors);
            }

            Product product = new Product();
            product.setSku(sku);
            product.setName(dto.getName());
            product.setPrice(new BigDecimal(dto.getPrice()));
            product.setQuantity(Integer.parseInt(dto.getQuantity()));
            product.setStatus(parseStatus(dto.getStatus()));
            product.setCategory(category); // chỉ gán tham chiếu, không persist category

            // Tạo Detail cùng lúc - cascade ALL từ Product nên persist một phát là cả hai được lưu.
            ProductDetail detail = new ProductDetail();
            detail.setManufacturer(dto.getManufacturer());
            detail.setWarrantyMonths(parseWarranty(dto.getWarrantyMonths()));
            detail.setOrigin(dto.getOrigin());
            detail.setDescription(dto.getDetailDescription());
            detail.setTechnicalSpec(dto.getTechnicalSpec());
            product.setDetail(detail); // đồng bộ cả 2 chiều quan hệ

            return productRepository.save(em, product);
        });
    }

    // ------------------------------------------------------------------
    // UPDATE Product + Category + Detail đồng bộ (1 transaction)
    // ------------------------------------------------------------------
    public Product update(ProductFormDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("Thiếu id sản phẩm cần cập nhật.");
        }
        Map<String, String> errors = validate(dto);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return JPAUtil.runInTransactionReturn(em -> {
            // Load kèm Detail bằng join fetch để sửa trong cùng persistence context.
            Product product = productRepository.findByIdWithDetail(em, dto.getId())
                    .orElseThrow(() -> new BusinessException("Sản phẩm không tồn tại hoặc đã bị xoá (id=" + dto.getId() + ")."));

            // SKU: bỏ trống thì giữ nguyên SKU cũ, nhập thì phải unique (trừ chính nó).
            if (dto.getSku() != null && !dto.getSku().isBlank()) {
                if (productRepository.existsBySkuAndIdNot(em, dto.getSku(), dto.getId())) {
                    errors.put("sku", "SKU '" + dto.getSku() + "' đã thuộc về sản phẩm khác.");
                    throw new ValidationException(errors); // rollback: Product không bị sửa nửa chừng
                }
                product.setSku(dto.getSku());
            }

            Category category = categoryRepository.findById(em, dto.getCategoryId())
                    .orElseThrow(() -> new BusinessException("Danh mục không tồn tại (id=" + dto.getCategoryId() + ")."));
            if (!category.isStatus()) {
                errors.put("categoryId", "Danh mục '" + category.getName() + "' đang bị vô hiệu hoá.");
                throw new ValidationException(errors);
            }

            product.setName(dto.getName());
            product.setPrice(new BigDecimal(dto.getPrice()));
            product.setQuantity(Integer.parseInt(dto.getQuantity()));
            product.setStatus(parseStatus(dto.getStatus()));
            product.setCategory(category);

            // Cập nhật Detail: tái sử dụng hàng đã có, chưa có thì tạo mới (vẫn cascade).
            ProductDetail detail = product.getDetail();
            if (detail == null) {
                detail = new ProductDetail();
                product.setDetail(detail);
            }
            detail.setManufacturer(dto.getManufacturer());
            detail.setWarrantyMonths(parseWarranty(dto.getWarrantyMonths()));
            detail.setOrigin(dto.getOrigin());
            detail.setDescription(dto.getDetailDescription());
            detail.setTechnicalSpec(dto.getTechnicalSpec());

            return productRepository.save(em, product); // entity đang managed => dirty checking tự flush
        });
    }

    // ------------------------------------------------------------------
    // SOFT DELETE - chỉ set deleted = true
    // ------------------------------------------------------------------
    public void softDelete(Long id) {
        JPAUtil.runInTransaction(em -> {
            Product product = productRepository.findById(em, id)
                    .orElseThrow(() -> new BusinessException("Sản phẩm không tồn tại (id=" + id + ")."));
            if (product.isDeleted()) {
                throw new BusinessException("Sản phẩm (id=" + id + ") đã bị xoá trước đó.");
            }
            productRepository.softDelete(em, id);
            // Không xoá Category, không xoá ProductDetail - giữ nguyên dữ liệu vật lý.
        });
    }

    // ------------------------------------------------------------------
    // VALIDATE nghiệp vụ (mục 6): tên, giá > 0, tồn kho >= 0, bảo hành >= 0...
    // ------------------------------------------------------------------
    public Map<String, String> validate(ProductFormDTO dto) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (dto.getName() == null || dto.getName().isBlank()) {
            errors.put("name", "Tên sản phẩm không được để trống.");
        } else if (dto.getName().length() > 200) {
            errors.put("name", "Tên sản phẩm tối đa 200 ký tự.");
        }

        // Giá > 0
        BigDecimal price = parseDecimal(dto.getPrice());
        if (price == null) {
            errors.put("price", "Giá phải là số hợp lệ và không được để trống.");
        } else if (price.signum() <= 0) {
            errors.put("price", "Giá phải lớn hơn 0.");
        }

        // Tồn kho >= 0
        Integer quantity = parseInteger(dto.getQuantity());
        if (quantity == null) {
            errors.put("quantity", "Tồn kho phải là số nguyên hợp lệ và không được để trống.");
        } else if (quantity < 0) {
            errors.put("quantity", "Tồn kho phải >= 0.");
        }

        // Danh mục bắt buộc
        if (dto.getCategoryId() == null) {
            errors.put("categoryId", "Vui lòng chọn danh mục cho sản phẩm.");
        }

        // Bảo hành >= 0 (bỏ trống coi như 0)
        Integer warranty = parseInteger(dto.getWarrantyMonths());
        if (warranty != null && warranty < 0) {
            errors.put("warrantyMonths", "Thời gian bảo hành phải >= 0.");
        }

        return errors;
    }

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------

    /** Sinh SKU tự động: SKU-20260914-103045-123 (ngày + giờ + số ngẫu nhiên). */
    private String generateSku() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000);
        return String.format("SKU-%s-%03d", timestamp, random);
    }

    private boolean parseStatus(String raw) {
        return raw != null && ("on".equalsIgnoreCase(raw) || "true".equalsIgnoreCase(raw) || "1".equals(raw));
    }

    private Integer parseWarranty(String raw) {
        Integer value = parseInteger(raw);
        return value == null ? 0 : value;
    }

    private BigDecimal parseDecimal(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(raw.replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
