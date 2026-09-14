package com.example.javawebmvc.dto;

import java.util.List;

/**
 * DTO kết quả phân trang dùng chung - không trả thẳng List thô kèm biến rời rạc.
 * totalItems / totalPages giúp JSP vẽ thanh phân trang chỉ bằng EL.
 */
public class PageResult<T> {

    private final List<T> items;
    private final long totalItems;
    private final int page;
    private final int size;

    public PageResult(List<T> items, long totalItems, int page, int size) {
        this.items = items;
        this.totalItems = totalItems;
        this.page = page;
        this.size = size;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalItems / size);
    }

    /** Trang kế tiếp có tồn tại không (để enable nút Next). */
    public boolean isHasNext() {
        return page < getTotalPages();
    }

    /** Trang trước có tồn tại không (để enable nút Previous). */
    public boolean isHasPrevious() {
        return page > 1;
    }

    // ----- Getter -----
    public List<T> getItems() { return items; }

    public long getTotalItems() { return totalItems; }

    public int getPage() { return page; }

    public int getSize() { return size; }
}
