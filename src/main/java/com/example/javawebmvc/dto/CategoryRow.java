package com.example.javawebmvc.dto;

/**
 * DTO hàng hiển thị cho bảng quản lý Category: thông tin danh mục
 * kèm số lượng Product active / tổng số - tránh đếm N+1 trong JSP.
 */
public class CategoryRow {

    private final Long id;
    private final String name;
    private final String description;
    private final boolean status;
    private final long activeProducts;
    private final long totalProducts;

    public CategoryRow(Long id, String name, String description, boolean status,
                       long activeProducts, long totalProducts) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.activeProducts = activeProducts;
        this.totalProducts = totalProducts;
    }

    // ----- Getter -----
    public Long getId() { return id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public boolean isStatus() { return status; }

    public long getActiveProducts() { return activeProducts; }

    public long getTotalProducts() { return totalProducts; }
}
