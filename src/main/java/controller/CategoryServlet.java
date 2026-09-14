package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import entity.Category;
import service.CategoryService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {

    private CategoryService categoryService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.categoryService = new CategoryService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        List<Category> categories = categoryService.getAllCategories();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/view/category.list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String statusStr = request.getParameter("status");

        try {
            if (idStr != null && !idStr.isEmpty()) {
                Long id = Long.parseLong(idStr);
                Category category = categoryService.getCategoryById(id).orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
                category.setName(name);
                category.setStatus("on".equals(statusStr));
                category.setUpdatedAt(LocalDateTime.now());
                categoryService.updateCategory(category);
            } else {
                Category category = new Category();
                category.setName(name);
                category.setStatus(true);
                category.setCreatedAt(LocalDateTime.now());
                category.setUpdatedAt(LocalDateTime.now());
                categoryService.createCategory(category);
            }

            response.sendRedirect(request.getContextPath() + "/categories");
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
            List<Category> categories = categoryService.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/view/category.list.jsp").forward(request, response);
        }
    }
}

