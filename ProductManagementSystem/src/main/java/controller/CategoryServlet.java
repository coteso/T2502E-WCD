package controller;

import service.CategoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("categories", categoryService.findAll());
        request.getRequestDispatcher("/views/category-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("disable".equals(action)) {
            try {
                Long id = Long.parseLong(request.getParameter("id"));
                categoryService.disableCategory(id);
                request.getSession().setAttribute("message", "Vô hiệu hoá danh mục thành công.");
            } catch (Exception e) {
                request.getSession().setAttribute("error", e.getMessage());
            }
        }
        response.sendRedirect(request.getContextPath() + "/categories");
    }
}