package com.example.javawebmvc.model;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    // Các thuộc tính ánh xạ chính xác từ bảng products
    private int id;
    private String name;
    private double price;
    private int quantity;
    private boolean status;
    private boolean deleted;
    private int categoryId; // Ánh xạ từ category_id (FOREIGN KEY)

    // 1. Constructor không tham số (Bắt buộc theo chuẩn JavaBean)
    public Product() {
    }

    // 2. Constructor đầy đủ tham số (Hỗ trợ khởi tạo nhanh)
    public Product(int id, String name, double price, int quantity, boolean status, boolean deleted, int categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.deleted = deleted;
        this.categoryId = categoryId;
    }

    // 3. Toàn bộ Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}

