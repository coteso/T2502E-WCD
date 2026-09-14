package com.example.javawebmvc.exception;

/**
 * Exception nghiệp vụ: SKU trùng, id không tồn tại, category không hợp lệ,
 * không được xoá/disable category khi còn product active, v.v.
 * Bắt ở Servlet để forward sang error.jsp kèm thông báo thân thiện.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
