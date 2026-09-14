package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;

import java.io.IOException;

@WebServlet(urlPatterns = {"/categories"})
public class CategoryServlet extends HttpServlet {
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("categories", categoryService.getAllCategories());
        req.getRequestDispatcher("/views/category-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long id = req.getParameter("id") != null && !req.getParameter("id").isEmpty() ? Long.parseLong(req.getParameter("id")) : null;
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            Boolean status = Boolean.parseBoolean(req.getParameter("status"));

            categoryService.saveOrUpdate(id, name, description, status);
            resp.sendRedirect(req.getContextPath() + "/categories");
        } catch (Exception e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("categories", categoryService.getAllCategories());
            req.getRequestDispatcher("/views/category-list.jsp").forward(req, resp);
        }
    }
}