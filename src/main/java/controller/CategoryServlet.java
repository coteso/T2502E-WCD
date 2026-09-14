package controller;

import entity.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;

import java.io.IOException;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("categories", categoryService.getAllCategories());
            req.getRequestDispatcher("/views/category-list.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("save".equals(action)) {
                String idParam = req.getParameter("id");
                String name = req.getParameter("name");
                String description = req.getParameter("description");
                boolean status = req.getParameter("status") != null;

                Category category = new Category();
                if (idParam != null && !idParam.isEmpty()) {
                    category = categoryService.getCategoryById(Long.parseLong(idParam))
                            .orElseThrow(() -> new Exception("Danh mục không tồn tại!"));
                }
                category.setName(name);
                category.setDescription(description);
                category.setStatus(status);

                categoryService.saveOrUpdate(category);
            } else if ("toggleStatus".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                Category category = categoryService.getCategoryById(id)
                        .orElseThrow(() -> new Exception("Danh mục không tồn tại!"));
                category.setStatus(!category.getStatus());
                categoryService.saveOrUpdate(category);
            }
            resp.sendRedirect(req.getContextPath() + "/categories?msg=success");
        } catch (Exception e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}