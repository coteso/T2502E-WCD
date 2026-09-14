package com.example.javawebmvc.entity;

import jakarta.persistence.*;

/**
 * ENTITY ProductDetail - thông tin chi tiết của sản phẩm.
 * Chiều CHỦ của quan hệ 1-1: giữ @JoinColumn product_id, được cascade
 * theo Product (Product.detail cấu hình mappedBy + CascadeType.ALL).
 */
@Entity
@Table(name = "product_details")
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Liên kết 1-1 với Product - nullable = false và unique = true
     * để đảm bảo mỗi Product chỉ có đúng 1 Detail.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "manufacturer", length = 150)
    private String manufacturer;

    /** Số tháng bảo hành, phải >= 0 (validate ở tầng Service). */
    @Column(name = "warranty_months", nullable = false)
    private Integer warrantyMonths = 0;

    @Column(name = "origin", length = 100)
    private String origin;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "technical_spec", columnDefinition = "TEXT")
    private String technicalSpec;

    public ProductDetail() {
    }

    // ----- Getter / Setter -----
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }

    public void setProduct(Product product) { this.product = product; }

    public String getManufacturer() { return manufacturer; }

    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public Integer getWarrantyMonths() { return warrantyMonths; }

    public void setWarrantyMonths(Integer warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    public String getOrigin() { return origin; }

    public void setOrigin(String origin) { this.origin = origin; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getTechnicalSpec() { return technicalSpec; }

    public void setTechnicalSpec(String technicalSpec) { this.technicalSpec = technicalSpec; }
}
