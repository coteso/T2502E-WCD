package controller;

import dto.ProductFormDTO;
import dto.ProductSearchDTO;
import entity.Category;
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

@WebServlet(name = "ProductServlet", urlPatterns = {
        "/products",
        "/products/create",
        "/products/edit",
        "/products/delete"
})
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/products/create":
                showCreateForm(request, response);
                break;
            case "/products/edit":
                showEditForm(request, response);
                break;
            case "/products":
            default:
                listProducts(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/products/create":
                handleCreateProduct(request, response);
                break;
            case "/products/edit":
                handleUpdateProduct(request, response);
                break;
            case "/products/delete":
                handleDeleteProduct(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/products");
                break;
        }
    }

    private void listProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductSearchDTO criteria = new ProductSearchDTO();

        String keyword = request.getParameter("keyword");
        if (keyword != null && !keyword.trim().isEmpty()) {
            criteria.setKeyword(keyword.trim());
        }

        String categoryIdStr = request.getParameter("categoryId");
        if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
            try {
                criteria.setCategoryId(Long.parseLong(categoryIdStr));
            } catch (NumberFormatException ignored) {}
        }

        String statusStr = request.getParameter("status");
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            if ("active".equalsIgnoreCase(statusStr)) {
                criteria.setStatus(true);
            } else if ("inactive".equalsIgnoreCase(statusStr)) {
                criteria.setStatus(false);
            }
        }

        String minPriceStr = request.getParameter("minPrice");
        if (minPriceStr != null && !minPriceStr.trim().isEmpty()) {
            try {
                criteria.setMinPrice(new BigDecimal(minPriceStr.trim()));
            } catch (NumberFormatException ignored) {}
        }

        String maxPriceStr = request.getParameter("maxPrice");
        if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
            try {
                criteria.setMaxPrice(new BigDecimal(maxPriceStr.trim()));
            } catch (NumberFormatException ignored) {}
        }

        String sortBy = request.getParameter("sortBy");
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            criteria.setSortBy(sortBy.trim());
        }

        String sortDir = request.getParameter("sortDir");
        if (sortDir != null && !sortDir.trim().isEmpty()) {
            criteria.setSortDir(sortDir.trim());
        }

        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                criteria.setPage(Integer.parseInt(pageStr));
            } catch (NumberFormatException ignored) {}
        }

        String sizeStr = request.getParameter("size");
        if (sizeStr != null && !sizeStr.trim().isEmpty()) {
            try {
                criteria.setSize(Integer.parseInt(sizeStr));
            } catch (NumberFormatException ignored) {}
        }

        List<Product> products = productService.searchProducts(criteria);
        long totalItems = productService.countProducts(criteria);
        int totalPages = (int) Math.ceil((double) totalItems / criteria.getSize());
        if (totalPages < 1) totalPages = 1;

        List<Category> categories = categoryService.getAllCategories();

        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.setAttribute("searchCriteria", criteria);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/views/product-list.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Category> categories = categoryService.getAllCategories();
        request.setAttribute("categories", categories);
        if (request.getAttribute("formDTO") == null) {
            request.setAttribute("formDTO", new ProductFormDTO());
        }
        request.setAttribute("isEdit", false);
        request.getRequestDispatcher("/views/product-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            showErrorPage(request, response, "ID sản phẩm không được cung cấp.");
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            ProductFormDTO formDTO = productService.getProductFormDTOById(id);
            List<Category> categories = categoryService.getAllCategories();

            request.setAttribute("formDTO", formDTO);
            request.setAttribute("categories", categories);
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/views/product-form.jsp").forward(request, response);
        } catch (Exception ex) {
            showErrorPage(request, response, ex.getMessage());
        }
    }

    private void handleCreateProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductFormDTO formDTO = extractFormDTO(request);
        try {
            productService.createProduct(formDTO);
            response.sendRedirect(request.getContextPath() + "/products?msg=created");
        } catch (Exception ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            request.setAttribute("formDTO", formDTO);
            showCreateForm(request, response);
        }
    }

    private void handleUpdateProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductFormDTO formDTO = extractFormDTO(request);
        try {
            productService.updateProduct(formDTO);
            response.sendRedirect(request.getContextPath() + "/products?msg=updated");
        } catch (Exception ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            request.setAttribute("formDTO", formDTO);
            List<Category> categories = categoryService.getAllCategories();
            request.setAttribute("categories", categories);
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/views/product-form.jsp").forward(request, response);
        }
    }

    private void handleDeleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        try {
            if (idStr == null || idStr.trim().isEmpty()) {
                throw new IllegalArgumentException("ID sản phẩm không được trống.");
            }
            Long id = Long.parseLong(idStr);
            productService.softDeleteProduct(id);
            response.sendRedirect(request.getContextPath() + "/products?msg=deleted");
        } catch (Exception ex) {
            showErrorPage(request, response, ex.getMessage());
        }
    }

    private ProductFormDTO extractFormDTO(HttpServletRequest request) {
        ProductFormDTO dto = new ProductFormDTO();

        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                dto.setId(Long.parseLong(idStr));
            } catch (NumberFormatException ignored) {}
        }

        dto.setSku(request.getParameter("sku"));
        dto.setName(request.getParameter("name"));

        String priceStr = request.getParameter("price");
        if (priceStr != null && !priceStr.trim().isEmpty()) {
            try {
                dto.setPrice(new BigDecimal(priceStr.trim()));
            } catch (NumberFormatException ignored) {}
        }

        String quantityStr = request.getParameter("quantity");
        if (quantityStr != null && !quantityStr.trim().isEmpty()) {
            try {
                dto.setQuantity(Integer.parseInt(quantityStr.trim()));
            } catch (NumberFormatException ignored) {}
        }

        String statusStr = request.getParameter("status");
        dto.setStatus("on".equalsIgnoreCase(statusStr) || "true".equalsIgnoreCase(statusStr));

        String categoryIdStr = request.getParameter("categoryId");
        if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
            try {
                dto.setCategoryId(Long.parseLong(categoryIdStr));
            } catch (NumberFormatException ignored) {}
        }

        // ProductDetail fields
        dto.setManufacturer(request.getParameter("manufacturer"));

        String warrantyStr = request.getParameter("warrantyMonths");
        if (warrantyStr != null && !warrantyStr.trim().isEmpty()) {
            try {
                dto.setWarrantyMonths(Integer.parseInt(warrantyStr.trim()));
            } catch (NumberFormatException ignored) {}
        }

        dto.setOrigin(request.getParameter("origin"));
        dto.setDetailDescription(request.getParameter("detailDescription"));
        dto.setTechnicalSpec(request.getParameter("technicalSpec"));

        return dto;
    }

    private void showErrorPage(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/views/error.jsp").forward(request, response);
    }
}
