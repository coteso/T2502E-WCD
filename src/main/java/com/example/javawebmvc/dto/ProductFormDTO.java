package com.example.javawebmvc.dto;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DTO nhận dữ liệu từ form thêm/sửa Product + ProductDetail (cùng 1 màn hình).
 * Các trường số giữ dạng String để báo lỗi định dạng thân thiện thay vì crash.
 * Servlet bind request vào DTO, Service validate và trả về Map<tenTruong, loi>.
 */
public class ProductFormDTO {

    private Long id;
    private String sku;
    private String name;
    private String price;          // dạng text, parse + validate ở Service
    private String quantity;
    private String status;         // "on"/"true" khi checkbox được chọn
    private Long categoryId;
    private String manufacturer;
    private String warrantyMonths; // dạng text, phải >= 0
    private String origin;
    private String detailDescription;
    private String technicalSpec;
    private Long detailId;         // id Detail cũ khi sửa (tái sử dụng hàng đã có)

    public ProductFormDTO() {
    }

    /** Sinh DTO từ request - dùng chung cho GET (pre-fill) và POST. */
    public static ProductFormDTO fromRequest(HttpServletRequest req) {
        ProductFormDTO dto = new ProductFormDTO();
        dto.setId(parseLong(req.getParameter("id")));
        dto.setSku(trim(req.getParameter("sku")));
        dto.setName(trim(req.getParameter("name")));
        dto.setPrice(trim(req.getParameter("price")));
        dto.setQuantity(trim(req.getParameter("quantity")));
        dto.setStatus(req.getParameter("status"));
        dto.setCategoryId(parseLong(req.getParameter("categoryId")));
        dto.setManufacturer(trim(req.getParameter("manufacturer")));
        dto.setWarrantyMonths(trim(req.getParameter("warrantyMonths")));
        dto.setOrigin(trim(req.getParameter("origin")));
        dto.setDetailDescription(trim(req.getParameter("detailDescription")));
        dto.setTechnicalSpec(trim(req.getParameter("technicalSpec")));
        dto.setDetailId(parseLong(req.getParameter("detailId")));
        return dto;
    }

    private static Long parseLong(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Long.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    // ----- Getter / Setter -----
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }

    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }

    public void setPrice(String price) { this.price = price; }

    public String getQuantity() { return quantity; }

    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Long getCategoryId() { return categoryId; }

    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getManufacturer() { return manufacturer; }

    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getWarrantyMonths() { return warrantyMonths; }

    public void setWarrantyMonths(String warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    public String getOrigin() { return origin; }

    public void setOrigin(String origin) { this.origin = origin; }

    public String getDetailDescription() { return detailDescription; }

    public void setDetailDescription(String detailDescription) { this.detailDescription = detailDescription; }

    public String getTechnicalSpec() { return technicalSpec; }

    public void setTechnicalSpec(String technicalSpec) { this.technicalSpec = technicalSpec; }

    public Long getDetailId() { return detailId; }

    public void setDetailId(Long detailId) { this.detailId = detailId; }
}
