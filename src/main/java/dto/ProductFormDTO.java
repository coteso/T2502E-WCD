package dto;

import java.math.BigDecimal;

/**
 * ProductFormDTO - Data Transfer Object cho form tạo/sửa sản phẩm
 * 
 * DTO (Data Transfer Object) là pattern dùng để:
 * - Transfer dữ liệu từ Servlet → Service → Repository
 * - Tránh truyền Entity class trực tiếp (tránh logic trong entity)
 * - Giúp validate dữ liệu dễ hơn
 * 
 * Form ProductFormDTO bao gồm thông tin Product + ProductDetail
 * Vì cả 2 được tạo/cập nhật cùng lúc trong 1 transaction
 */
public class ProductFormDTO {
    
    // ===== PRODUCT FIELDS =====
    private Long productId;        // null nếu tạo mới, có giá trị nếu sửa
    private String sku;            // SKU phải duy nhất
    private String name;           // Tên sản phẩm
    private BigDecimal price;      // Giá (> 0)
    private Integer quantity;      // Tồn kho (>= 0)
    private String description;    // Mô tả sản phẩm
    private Long categoryId;       // Category ID (bắt buộc)
    private Boolean status;        // Trạng thái hoạt động

    // ===== PRODUCT DETAIL FIELDS =====
    private Long detailId;         // null nếu tạo mới
    private String manufacturer;   // Nhà sản xuất
    private Integer warrantyMonths; // Thời gian bảo hành (tháng)
    private String origin;         // Xuất xứ
    private String detailDescription; // Mô tả chi tiết
    private String technicalSpec;  // Thông số kỹ thuật

    public ProductFormDTO() {
    }

    // Constructor
    public ProductFormDTO(String sku, String name, BigDecimal price, Integer quantity,
                         String description, Long categoryId, Boolean status,
                         String manufacturer, Integer warrantyMonths, String origin) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.categoryId = categoryId;
        this.status = status;
        this.manufacturer = manufacturer;
        this.warrantyMonths = warrantyMonths;
        this.origin = origin;
    }

    // Getter & Setter
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDetailDescription() {
        return detailDescription;
    }

    public void setDetailDescription(String detailDescription) {
        this.detailDescription = detailDescription;
    }

    public String getTechnicalSpec() {
        return technicalSpec;
    }

    public void setTechnicalSpec(String technicalSpec) {
        this.technicalSpec = technicalSpec;
    }
}
