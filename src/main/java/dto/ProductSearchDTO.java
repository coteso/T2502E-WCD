package dto;

import java.math.BigDecimal;

/**
 * ProductSearchDTO - Data Transfer Object cho tìm kiếm/lọc/phân trang sản phẩm
 * 
 * Bao gồm các tiêu chí tìm kiếm:
 * - keyword: Tìm theo tên hoặc SKU
 * - categoryId: Lọc theo danh mục
 * - status: Lọc theo trạng thái
 * - minPrice, maxPrice: Lọc theo khoảng giá
 * - sortBy: Sắp xếp theo cột nào (name, price, createdAt)
 * - sortDir: Chiều sắp xếp (asc, desc)
 * - page, size: Phân trang
 */
public class ProductSearchDTO {
    
    // ===== SEARCH/FILTER CRITERIA =====
    private String keyword;         // Tìm kiếm theo name hoặc sku
    private Long categoryId;        // Lọc theo category
    private Boolean status;         // Lọc theo status (true=active, false=inactive)
    private BigDecimal minPrice;    // Giá tối thiểu
    private BigDecimal maxPrice;    // Giá tối đa

    // ===== SORTING =====
    private String sortBy;          // Trường sắp xếp: name, price, createdAt (mặc định: createdAt)
    private String sortDir;         // Chiều sắp xếp: asc (tăng dần) hoặc desc (giảm dần)

    // ===== PAGINATION =====
    private Integer page;           // Số trang (bắt đầu từ 0 hoặc 1 tuỳ thiết kế)
    private Integer size;           // Số item trên mỗi trang (mặc định: 10)

    public ProductSearchDTO() {
        // Giá trị mặc định
        this.sortBy = "createdAt";
        this.sortDir = "desc";
        this.page = 0;
        this.size = 10;
    }

    // Constructor đầy đủ
    public ProductSearchDTO(String keyword, Long categoryId, Boolean status,
                           BigDecimal minPrice, BigDecimal maxPrice,
                           String sortBy, String sortDir, Integer page, Integer size) {
        this.keyword = keyword;
        this.categoryId = categoryId;
        this.status = status;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.sortBy = sortBy != null ? sortBy : "createdAt";
        this.sortDir = sortDir != null ? sortDir : "desc";
        this.page = page != null ? page : 0;
        this.size = size != null ? size : 10;
    }

    // Getter & Setter
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy != null ? sortBy : "createdAt";
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir != null ? sortDir : "desc";
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page != null ? page : 0;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size != null ? size : 10;
    }

    /**
     * Tính offset cho JPQL OFFSET clause
     * Ví dụ: page=0, size=10 → offset=0 (lấy item 0-9)
     *        page=1, size=10 → offset=10 (lấy item 10-19)
     */
    public Integer getOffset() {
        return page * size;
    }

    @Override
    public String toString() {
        return "ProductSearchDTO{" +
                "keyword='" + keyword + '\'' +
                ", categoryId=" + categoryId +
                ", status=" + status +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", sortBy='" + sortBy + '\'' +
                ", sortDir='" + sortDir + '\'' +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}
