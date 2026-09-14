package dto;

import java.math.BigDecimal;

public class ProductFormDTO {
    private Long id;
    private String sku;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Boolean status;
    private Long categoryId;

    // Detail Fields
    private String manufacturer;
    private Integer warrantyMonths;
    private String origin;
    private String description;
    private String technicalSpec;

    // Getters and Setters
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
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
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