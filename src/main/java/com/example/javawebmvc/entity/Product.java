package com.example.javawebmvc.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ENTITY Product - sản phẩm, thuộc 1 Category và có tối đa 1 ProductDetail.
 * Quan hệ: Product n - 1 Category (ManyToOne LAZY) và Product 1 - 1 ProductDetail
 * (chiều chủ là ProductDetail, ở đây chỉ là chiều "mappedBy").
 *
 * Soft delete: không xoá dòng dữ liệu, chỉ đặt deleted = true.
 */
@Entity
@Table(name = "products",
        uniqueConstraints = @UniqueConstraint(name = "uk_products_sku", columnNames = "sku"))
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mã sản phẩm - duy nhất toàn hệ thống.
     * Nếu form để trống, Service sẽ tự sinh theo mẫu "SKU-yyyymmdd-HHmmss-xxx".
     */
    @Column(name = "sku", nullable = false, unique = true, length = 64)
    private String sku;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /** true = active (đang bán), false = inactive (ngừng bán). */
    @Column(name = "status", nullable = false)
    private boolean status = true;

    /** Cờ xoá mềm - khi true, bản ghi vẫn còn trong DB nhưng ẩn khỏi danh sách. */
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Chiều "mappedBy" của ProductDetail.product - cascade ALL + orphanRemoval
     * để lưu/xoá Detail cùng vòng đời Product trong MỘT transaction.
     */
    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private ProductDetail detail;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Product() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ----- Getter / Setter -----
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }

    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }

    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getQuantity() { return quantity; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public boolean isStatus() { return status; }

    public void setStatus(boolean status) { this.status = status; }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public Category getCategory() { return category; }

    public void setCategory(Category category) { this.category = category; }

    public ProductDetail getDetail() { return detail; }

    public void setDetail(ProductDetail detail) {
        this.detail = detail;
        if (detail != null) {
            detail.setProduct(this);
        }
    }

    /** Tiện dụng cho EL trong JSP: tên danh mục mà không cần lazy-load thủ công. */
    public String getCategoryName() {
        return category != null ? category.getName() : null;
    }
}
