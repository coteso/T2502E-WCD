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
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {
        "/products",
        "/products/create",
        "/products/edit",
        "/products/delete"
})
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
            forwardError(req, resp, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        try {
            switch (path) {
                case "/products/create":
                    handleCreateProduct(req, resp);
                    break;
                case "/products/edit":
                    handleUpdateProduct(req, resp);
                    break;
                case "/products/delete":
                    handleDeleteProduct(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/products");
                    break;
            }
        } catch (Exception e) {
            forwardError(req, resp, e.getMessage());
        }
    }

    private void listProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductSearchDTO criteria = new ProductSearchDTO();
        criteria.setKeyword(req.getParameter("keyword"));

        String catIdParam = req.getParameter("categoryId");
        if (catIdParam != null && !catIdParam.isEmpty()) {
            criteria.setCategoryId(Long.parseLong(catIdParam));
        }

        String statusParam = req.getParameter("status");
        if (statusParam != null && !statusParam.isEmpty()) {
            criteria.setStatus(Boolean.parseBoolean(statusParam));
        }

        String minPriceParam = req.getParameter("minPrice");
        if (minPriceParam != null && !minPriceParam.isEmpty()) {
            criteria.setMinPrice(Double.parseDouble(minPriceParam));
        }

        String maxPriceParam = req.getParameter("maxPrice");
        if (maxPriceParam != null && !maxPriceParam.isEmpty()) {
            criteria.setMaxPrice(Double.parseDouble(maxPriceParam));
        }

        String sortBy = req.getParameter("sortBy");
        if (sortBy != null && !sortBy.isEmpty()) {
            criteria.setSortBy(sortBy);
        }

        String sortDir = req.getParameter("sortDir");
        if (sortDir != null && !sortDir.isEmpty()) {
            criteria.setSortDir(sortDir);
        }

        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            criteria.setPage(Integer.parseInt(pageParam));
        }

        List<Product> products = productService.searchProducts(criteria);
        long totalItems = productService.countProducts(criteria);
        int totalPages = (int) Math.ceil((double) totalItems / criteria.getSize());

        req.setAttribute("products", products);
        req.setAttribute("totalItems", totalItems);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("criteria", criteria);
        req.setAttribute("categories", categoryService.getActiveCategories());

        req.getRequestDispatcher("/views/product-list.jsp").forward(req, resp);
    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("categories", categoryService.getActiveCategories());
        req.getRequestDispatcher("/views/product-form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            throw new Exception("Thiếu ID sản phẩm cần chỉnh sửa!");
        }

        Long id = Long.parseLong(idParam);
        Optional<Product> opt = productService.getProductByIdWithDetail(id);
        if (opt.isEmpty()) {
            throw new Exception("Sản phẩm có ID " + id + " không tồn tại hoặc đã bị xóa!");
        }

        req.setAttribute("product", opt.get());
        req.setAttribute("categories", categoryService.getActiveCategories());
        req.getRequestDispatcher("/views/product-form.jsp").forward(req, resp);
    }

    private void handleCreateProduct(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ProductFormDTO dto = extractFormDTO(req);
        productService.createProduct(dto);
        resp.sendRedirect(req.getContextPath() + "/products?msg=created");
    }

    private void handleUpdateProduct(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ProductFormDTO dto = extractFormDTO(req);
        productService.updateProduct(dto);
        resp.sendRedirect(req.getContextPath() + "/products?msg=updated");
    }

    private void handleDeleteProduct(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            throw new Exception("Thiếu ID sản phẩm cần xóa!");
        }
        productService.softDelete(Long.parseLong(idParam));
        resp.sendRedirect(req.getContextPath() + "/products?msg=deleted");
    }

    private ProductFormDTO extractFormDTO(HttpServletRequest req) {
        ProductFormDTO dto = new ProductFormDTO();
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            dto.setId(Long.parseLong(idParam));
        }
        dto.setSku(req.getParameter("sku"));
        dto.setName(req.getParameter("name"));

        String priceParam = req.getParameter("price");
        if (priceParam != null && !priceParam.isEmpty()) {
            dto.setPrice(Double.parseDouble(priceParam));
        }

        String quantityParam = req.getParameter("quantity");
        if (quantityParam != null && !quantityParam.isEmpty()) {
            dto.setQuantity(Integer.parseInt(quantityParam));
        }

        String catIdParam = req.getParameter("categoryId");
        if (catIdParam != null && !catIdParam.isEmpty()) {
            dto.setCategoryId(Long.parseLong(catIdParam));
        }

        dto.setStatus(req.getParameter("status") != null);

        // ProductDetail attributes
        dto.setManufacturer(req.getParameter("manufacturer"));
        String warrantyParam = req.getParameter("warrantyMonths");
        if (warrantyParam != null && !warrantyParam.isEmpty()) {
            dto.setWarrantyMonths(Integer.parseInt(warrantyParam));
        }
        dto.setOrigin(req.getParameter("origin"));
        dto.setDescription(req.getParameter("description"));
        dto.setTechnicalSpec(req.getParameter("technicalSpec"));

        return dto;
    }

    private void forwardError(HttpServletRequest req, HttpServletResponse resp, String message) throws ServletException, IOException {
        req.setAttribute("errorMessage", message);
        req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
    }
}