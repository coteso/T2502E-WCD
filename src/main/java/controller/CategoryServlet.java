package controller;

import entity.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CategoryServlet", urlPatterns = {"/categories"})
public class CategoryServlet extends HttpServlet {

    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String editIdStr = request.getParameter("editId");
        if (editIdStr != null && !editIdStr.trim().isEmpty()) {
            try {
                Long editId = Long.parseLong(editIdStr);
                Category editCategory = categoryService.getCategoryById(editId);
                request.setAttribute("editCategory", editCategory);
            } catch (Exception ex) {
                request.setAttribute("errorMessage", ex.getMessage());
            }
        }

        loadCategoriesAndForward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) action = "save";

        try {
            switch (action) {
                case "toggle":
                    handleToggleStatus(request);
                    response.sendRedirect(request.getContextPath() + "/categories");
                    return;
                case "update":
                    handleUpdateCategory(request);
                    response.sendRedirect(request.getContextPath() + "/categories");
                    return;
                case "save":
                default:
                    handleSaveCategory(request);
                    response.sendRedirect(request.getContextPath() + "/categories");
                    return;
            }
        } catch (Exception ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            loadCategoriesAndForward(request, response);
        }
    }

    private void handleSaveCategory(HttpServletRequest request) {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        Boolean status = "on".equals(request.getParameter("status")) || "true".equalsIgnoreCase(request.getParameter("status"));

        categoryService.saveCategory(name, description, status);
    }

    private void handleUpdateCategory(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        Long id = Long.parseLong(idStr);
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        Boolean status = "on".equals(request.getParameter("status")) || "true".equalsIgnoreCase(request.getParameter("status"));

        categoryService.updateCategory(id, name, description, status);
    }

    private void handleToggleStatus(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        Long id = Long.parseLong(idStr);
        categoryService.toggleCategoryStatus(id);
    }

    private void loadCategoriesAndForward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Category> categories = categoryService.getAllCategories();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/views/category-list.jsp").forward(request, response);
    }
}
