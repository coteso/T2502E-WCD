package com.example.javawebmvc.dto;

/**
 * DTO chứa tiêu chí tìm kiếm / lọc / sắp xếp / phân trang cho danh sách Product.
 * Được bind từ query string của GET /products và truyền xuống Repository.
 */
public class ProductSearchDTO {

    /** Tìm theo tên HOẶC mã SKU (LIKE %keyword%). */
    private String keyword;

    /** Lọc theo danh mục (null/blank = tất cả). */
    private Long categoryId;

    /** "active" | "inactive" | null (tất cả). */
    private String status;

    /** Lọc khoảng giá [minPrice, maxPrice]. */
    private java.math.BigDecimal minPrice;
    private java.math.BigDecimal maxPrice;

    /** "name" | "price" | "createdAt" - mặc định "createdAt". */
    private String sortBy = "createdAt";

    /** "asc" | "desc" - mặc định "desc". */
    private String sortDir = "desc";

    /** Trang hiện tại, bắt đầu từ 1 - mặc định 1. */
    private int page = 1;

    /** Số dòng mỗi trang - mặc định 5. */
    private int size = 5;

    public ProductSearchDTO() {
    }

    /** Chuẩn hoá dữ liệu đầu vào: chữ thường cho keyword, giới hạn giá trị sort, page >= 1. */
    public void normalize() {
        if (keyword != null) {
            keyword = keyword.trim();
            if (keyword.isEmpty()) {
                keyword = null;
            }
        }
        if (!"name".equals(sortBy) && !"price".equals(sortBy) && !"createdAt".equals(sortBy)) {
            sortBy = "createdAt";
        }
        if (!"asc".equalsIgnoreCase(sortDir)) {
            sortDir = "desc";
        }
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 5;
        }
        if (status != null && status.isBlank()) {
            status = null;
        }
    }

    /** offset dùng cho setFirstResult của JPQL/Criteria. */
    public int getOffset() {
        return (page - 1) * size;
    }

    // ----- Getter / Setter -----
    public String getKeyword() { return keyword; }

    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Long getCategoryId() { return categoryId; }

    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public java.math.BigDecimal getMinPrice() { return minPrice; }

    public void setMinPrice(java.math.BigDecimal minPrice) { this.minPrice = minPrice; }

    public java.math.BigDecimal getMaxPrice() { return maxPrice; }

    public void setMaxPrice(java.math.BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    public String getSortBy() { return sortBy; }

    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDir() { return sortDir; }

    public void setSortDir(String sortDir) { this.sortDir = sortDir; }

    public int getPage() { return page; }

    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }

    public void setSize(int size) { this.size = size; }
}
