package controller;

import dto.ProductFormDTO;
import dto.ProductSearchDTO;
import entity.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;
import service.ProductService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(urlPatterns = {"/products", "/products/create", "/products/edit", "/products/delete"})
public class ProductServlet extends HttpServlet {
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            switch (path) {
                case "/products/create":
                    showCreateForm(req, resp);
                    break;
                case "/products/edit":
                    showEditForm(req, resp);
                    break;
                case "/products":
                default:
                    listProducts(req, resp);
                    break;
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            switch (path) {
                case "/products/create":
                case "/products/edit":
                    saveProduct(req, resp);
                    break;
                case "/products/delete":
                    deleteProduct(req, resp);
                    break;
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("categories", categoryService.getAllCategories());
            req.getRequestDispatcher("/views/product-form.jsp").forward(req, resp);
        }
    }

    private void listProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductSearchDTO criteria = new ProductSearchDTO();
        criteria.setKeyword(req.getParameter("keyword"));

        if (req.getParameter("categoryId") != null && !req.getParameter("categoryId").isEmpty()) {
            criteria.setCategoryId(Long.parseLong(req.getParameter("categoryId")));
        }
        if (req.getParameter("status") != null && !req.getParameter("status").isEmpty()) {
            criteria.setStatus(Boolean.parseBoolean(req.getParameter("status")));
        }
        if (req.getParameter("minPrice") != null && !req.getParameter("minPrice").isEmpty()) {
            criteria.setMinPrice(new BigDecimal(req.getParameter("minPrice")));
        }
        if (req.getParameter("maxPrice") != null && !req.getParameter("maxPrice").isEmpty()) {
            criteria.setMaxPrice(new BigDecimal(req.getParameter("maxPrice")));
        }
        if (req.getParameter("sortBy") != null) criteria.setSortBy(req.getParameter("sortBy"));
        if (req.getParameter("sortDir") != null) criteria.setSortDir(req.getParameter("sortDir"));
        if (req.getParameter("page") != null) criteria.setPage(Integer.parseInt(req.getParameter("page")));

        List<Product> products = productService.searchProducts(criteria);
        long totalItems = productService.countProducts(criteria);
        int totalPages = (int) Math.ceil((double) totalItems / criteria.getSize());

        req.setAttribute("products", products);
        req.setAttribute("categories", categoryService.getAllCategories());
        req.setAttribute("criteria", criteria);
        req.setAttribute("totalPages", totalPages);

        req.getRequestDispatcher("/views/product-list.jsp").forward(req, resp);
    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("categories", categoryService.getAllCategories());
        req.setAttribute("formDTO", new ProductFormDTO());
        req.getRequestDispatcher("/views/product-form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        Product p = productService.getById(id);

        ProductFormDTO dto = new ProductFormDTO();
        dto.setId(p.getId());
        dto.setSku(p.getSku());
        dto.setName(p.getName());
        dto.setPrice(p.getPrice());
        dto.setQuantity(p.getQuantity());
        dto.setStatus(p.getStatus());
        dto.setCategoryId(p.getCategory().getId());

        if (p.getDetail() != null) {
            dto.setManufacturer(p.getDetail().getManufacturer());
            dto.setWarrantyMonths(p.getDetail().getWarrantyMonths());
            dto.setOrigin(p.getDetail().getOrigin());
            dto.setDescription(p.getDetail().getDescription());
            dto.setTechnicalSpec(p.getDetail().getTechnicalSpec());
        }

        req.setAttribute("formDTO", dto);
        req.setAttribute("categories", categoryService.getAllCategories());
        req.getRequestDispatcher("/views/product-form.jsp").forward(req, resp);
    }

    private void saveProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductFormDTO dto = new ProductFormDTO();
        if (req.getParameter("id") != null && !req.getParameter("id").isEmpty()) {
            dto.setId(Long.parseLong(req.getParameter("id")));
        }
        dto.setSku(req.getParameter("sku"));
        dto.setName(req.getParameter("name"));
        dto.setPrice(req.getParameter("price") != null && !req.getParameter("price").isEmpty() ? new BigDecimal(req.getParameter("price")) : null);
        dto.setQuantity(req.getParameter("quantity") != null && !req.getParameter("quantity").isEmpty() ? Integer.parseInt(req.getParameter("quantity")) : null);
        dto.setStatus(Boolean.parseBoolean(req.getParameter("status")));
        dto.setCategoryId(req.getParameter("categoryId") != null && !req.getParameter("categoryId").isEmpty() ? Long.parseLong(req.getParameter("categoryId")) : null);

        // Details
        dto.setManufacturer(req.getParameter("manufacturer"));
        dto.setWarrantyMonths(req.getParameter("warrantyMonths") != null && !req.getParameter("warrantyMonths").isEmpty() ? Integer.parseInt(req.getParameter("warrantyMonths")) : null);
        dto.setOrigin(req.getParameter("origin"));
        dto.setDescription(req.getParameter("description"));
        dto.setTechnicalSpec(req.getParameter("technicalSpec"));

        productService.saveProduct(dto);
        resp.sendRedirect(req.getContextPath() + "/products");
    }

    private void deleteProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        productService.softDelete(id);
        resp.sendRedirect(req.getContextPath() + "/products");
    }
}