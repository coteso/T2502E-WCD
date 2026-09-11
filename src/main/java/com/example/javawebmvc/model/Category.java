package com.example.javawebmvc.model;

import java.io.Serializable;

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    // Các thuộc tính ánh xạ chính xác từ bảng categories
    private int id;
    private String name;
    private boolean status;

    // 1. Constructor không tham số (Chuẩn JavaBean bắt buộc)
    public Category() {
    }

    // 2. Constructor đầy đủ tham số
    public Category(int id, String name, boolean status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    // 3. Toàn bộ các Getter và Setter
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
