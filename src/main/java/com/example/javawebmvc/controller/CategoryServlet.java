package com.example.javawebmvc.controller;

import com.example.javawebmvc.dto.CategoryRow;
import com.example.javawebmvc.exception.BusinessException;
import com.example.javawebmvc.service.CategoryService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * CONTROLLER cho Category.
 *
 * URL mapping (mục 9 của đề):
 *   GET  /categories  -> danh sách category kèm số product active/total
 *   POST /categories  -> thêm / sửa / đổi trạng thái / xoá category
 *                        (phân biệt bằng tham số action=save|update|status|delete)
 */
@WebServlet(name = "CategoryServlet", urlPatterns = {"/categories"}, loadOnStartup = 1)
public class CategoryServlet extends HttpServlet {

    private CategoryService categoryService;

    @Override
    public void init() {
        categoryService = new CategoryService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            List<CategoryRow> rows = categoryService.listWithCounts();
            req.setAttribute("listCategories", rows);

            // Hỗ trợ nút "Sửa": đổ dữ liệu category lên form đầu trang.
            String editId = req.getParameter("editId");
            if (editId != null && !editId.isBlank()) {
                try {
                    req.setAttribute("editCategory", categoryService.get(Long.valueOf(editId)));
                } catch (NumberFormatException e) {
                    throw new BusinessException("editId không hợp lệ: " + editId);
                }
            }

            req.getRequestDispatcher("/WEB-INF/views/category-list.jsp").forward(req, resp);
        } catch (BusinessException e) {
            showError(req, resp, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "save" -> categoryService.create(
                        req.getParameter("name"), req.getParameter("description"));
                case "update" -> categoryService.update(
                        requireId(req), req.getParameter("name"), req.getParameter("description"));
                case "status" -> categoryService.setStatus(
                        requireId(req), Boolean.parseBoolean(req.getParameter("status")));
                case "delete" -> categoryService.delete(requireId(req));
                default -> throw new BusinessException("Hành động không hợp lệ: " + action);
            }
            req.getSession().setAttribute("flash", "Thao tác danh mục thành công.");
        } catch (BusinessException e) {
            // Ràng buộc "không xoá/disable khi còn product" hoặc tên trùng -> báo ngay trên trang category.
            req.getSession().setAttribute("flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/categories");
    }

    private Long requireId(HttpServletRequest req) {
        String raw = req.getParameter("id");
        if (raw == null || raw.isBlank()) {
            throw new BusinessException("Thiếu id danh mục.");
        }
        try {
            return Long.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            throw new BusinessException("id danh mục không hợp lệ: " + raw);
        }
    }

    private void showError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("errorTitle", "Lỗi danh mục");
        req.setAttribute("errorMessage", message);
        req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
    }
}
