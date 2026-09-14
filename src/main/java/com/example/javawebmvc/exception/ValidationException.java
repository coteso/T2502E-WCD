package com.example.javawebmvc.exception;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ném ra khi form không pass validate - mang theo Map<tenTruong, thongBaoLoi>
 * để Servlet forward ngược lại form và JSP hiển thị lỗi cạnh từng ô input.
 */
public class ValidationException extends BusinessException {

    private final Map<String, String> errors;

    public ValidationException(Map<String, String> errors) {
        super(errors.isEmpty() ? "Dữ liệu không hợp lệ." : errors.values().iterator().next());
        this.errors = Collections.unmodifiableMap(new LinkedHashMap<>(errors));
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
