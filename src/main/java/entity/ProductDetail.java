package entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "product_details")
public class ProductDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String manufacturer;

    @Column(nullable = false)
    private Integer warrantyMonths;

    @Column(nullable = false, length = 100)
    private String origin;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String technicalSpec;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    public ProductDetail() {}

    public ProductDetail(String manufacturer, Integer warrantyMonths, String origin, String description, String technicalSpec, Product product) {
        this.manufacturer = manufacturer;
        this.warrantyMonths = warrantyMonths;
        this.origin = origin;
        this.description = description;
        this.technicalSpec = technicalSpec;
        this.product = product;
    }

    // getters/setters
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
