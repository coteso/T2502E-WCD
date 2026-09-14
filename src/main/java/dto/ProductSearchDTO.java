package dto;

public class ProductSearchDTO {
    private String keyword;
    private Long categoryId;
    private Boolean status;
    private Double minPrice;
    private Double maxPrice;
    private String sortBy = "createdAt"; // createdAt, price, name
    private String sortDir = "desc";     // asc, desc
    private int page = 1;
    private int size = 5;

    public ProductSearchDTO() {}

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }
    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page <= 0 ? 1 : page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size <= 0 ? 5 : size; }
}