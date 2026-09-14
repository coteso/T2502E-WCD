package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_details")
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String manufacturer;

    @Column(name = "warranty_months")
    private Integer warrantyMonths;

    private String origin;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "technical_spec", columnDefinition = "TEXT")
    private String technicalSpec;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    public ProductDetail() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}