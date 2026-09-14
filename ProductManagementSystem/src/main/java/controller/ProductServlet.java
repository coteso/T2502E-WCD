package controller;

import dto.ProductFormDTO;
import dto.ProductSearchDTO;
import service.CategoryService;
import service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet({"/products", "/products/create", "/products/edit", "/products/delete"})
public class ProductServlet extends HttpServlet {
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        try {
            switch (path) {
                case "/products/create":
                    showForm(request, response);
                    break;
                case "/products/edit":
                    showEditForm(request, response);
                    break;
                default:
                    listProducts(request, response);
                    break;
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        try {
            if ("/products/delete".equals(path)) {
                Long id = Long.parseLong(request.getParameter("id"));
                productService.deleteSoftly(id);
                response.sendRedirect(request.getContextPath() + "/products");
                return;
            }

            ProductFormDTO dto = extractFormDTO(request);
            if ("/products/create".equals(path)) {
                productService.createProduct(dto);
            } else if ("/products/edit".equals(path)) {
                productService.updateProduct(dto);
            }
            response.sendRedirect(request.getContextPath() + "/products");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("product", extractFormDTO(request));
            request.setAttribute("categories", categoryService.findAll());
            request.getRequestDispatcher("/views/product-form.jsp").forward(request, response);
        }
    }

    private void listProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductSearchDTO searchDTO = new ProductSearchDTO();
        searchDTO.setKeyword(request.getParameter("keyword"));

        String catId = request.getParameter("categoryId");
        if (catId != null && !catId.isEmpty()) searchDTO.setCategoryId(Long.parseLong(catId));

        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) searchDTO.setPage(Integer.parseInt(pageStr));

        request.setAttribute("products", productService.search(searchDTO));
        request.setAttribute("totalPages", (int) Math.ceil((double) productService.count(searchDTO) / searchDTO.getSize()));
        request.setAttribute("categories", categoryService.findAll());
        request.getRequestDispatcher("/views/product-list.jsp").forward(request, response);
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("categories", categoryService.findAll());
        request.getRequestDispatcher("/views/product-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long id = Long.parseLong(request.getParameter("id"));
        request.setAttribute("product", productService.getProductForEdit(id));
        request.setAttribute("categories", categoryService.findAll());
        request.getRequestDispatcher("/views/product-form.jsp").forward(request, response);
    }

    private ProductFormDTO extractFormDTO(HttpServletRequest request) {
        ProductFormDTO dto = new ProductFormDTO();
        String id = request.getParameter("id");
        if (id != null && !id.isEmpty()) dto.setId(Long.parseLong(id));
        dto.setSku(request.getParameter("sku"));
        dto.setName(request.getParameter("name"));
        dto.setPrice(new BigDecimal(request.getParameter("price")));
        dto.setQuantity(Integer.parseInt(request.getParameter("quantity")));
        dto.setCategoryId(Long.parseLong(request.getParameter("categoryId")));
        dto.setStatus("true".equals(request.getParameter("status")));

        dto.setManufacturer(request.getParameter("manufacturer"));
        dto.setWarrantyMonths(Integer.parseInt(request.getParameter("warrantyMonths")));
        dto.setOrigin(request.getParameter("origin"));
        dto.setDescription(request.getParameter("description"));
        dto.setTechnicalSpec(request.getParameter("technicalSpec"));
        return dto;
    }
}