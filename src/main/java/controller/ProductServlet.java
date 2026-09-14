package controller;

import dto.ProductFormDTO;
import dto.ProductSearchDTO;
import entity.Category;
import entity.Product;
import service.CategoryService;
import service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * ProductServlet - HTTP Handler cho Product
 * 
 * Trách nhiệm:
 * 1. Nhận request từ client (HTTP)
 * 2. Parse dữ liệu từ request parameters
 * 3. Gọi ProductService để xử lý business logic
 * 4. Set attributes vào request (dữ liệu truyền tới JSP)
 * 5. Forward/Redirect tới JSP view
 * 
 * Không chứa business logic - chỉ là middleware giữa HTTP và Service layer
 */
@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    
    private ProductService productService;
    private CategoryService categoryService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Khởi tạo service (thường làm 1 lần khi servlet được load)
        this.productService = new ProductService();
        this.categoryService = new CategoryService();
    }

    /**
     * GET request handler
     * 
     * Hỗ trợ các hành động:
     * - GET /products: Hiển thị danh sách (với tìm kiếm, lọc, phân trang)
     * - GET /products/create: Hiển thị form tạo mới
     * - GET /products/edit?id=1: Hiển thị form sửa
     * - POST /products/delete?id=1: Xóa mềm sản phẩm
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        
        try {
            if ("new".equals(action) || "create".equals(action)) {
                // Hiển thị form tạo mới
                showForm(request, response, null);
            } else if ("edit".equals(action)) {
                // Hiển thị form sửa
                String idStr = request.getParameter("id");
                if (idStr == null || idStr.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu ID sản phẩm");
                    return;
                }
                
                Long id = Long.parseLong(idStr);
                Optional<Product> productOpt = productService.getProductWithDetail(id);
                
                if (!productOpt.isPresent()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Sản phẩm không tồn tại");
                    return;
                }
                
                showForm(request, response, productOpt.get());
            } else if ("delete".equals(action)) {
                // Xử lý xóa (sử dụng GET để tương thích với link hiện tại)
                String idStr = request.getParameter("id");
                if (idStr == null || idStr.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu ID sản phẩm");
                    return;
                }
                Long id = Long.parseLong(idStr);
                productService.deleteProduct(id);
                response.sendRedirect(request.getContextPath() + "/products");
                return;
            } else {
                // Hiển thị danh sách (mặc định)
                listProducts(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ");
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    /**
     * POST request handler - Xử lý form submit
     * 
     * Hỗ trợ:
     * - POST /products/create: Tạo sản phẩm mới
     * - POST /products/edit: Cập nhật sản phẩm
     * - POST /products/delete: Xóa mềm sản phẩm
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        
        try {
            if ("delete".equals(action)) {
                // Xóa mềm sản phẩm
                Long id = Long.parseLong(request.getParameter("id"));
                productService.deleteProduct(id);
                response.sendRedirect(request.getContextPath() + "/products");
            } else {
                // Tạo hoặc cập nhật sản phẩm
                saveProduct(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dữ liệu không hợp lệ");
        } catch (RuntimeException e) {
            // Business logic error (SKU trùng, category không hợp lệ, v.v.)
            request.setAttribute("error", e.getMessage());
            showForm(request, response, null);
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    /**
     * Hiển thị danh sách sản phẩm (với tìm kiếm, lọc, phân trang)
     */
    private void listProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lấy tham số tìm kiếm/lọc từ query string
        String keyword = request.getParameter("keyword");
        String categoryIdStr = request.getParameter("categoryId");
        String statusStr = request.getParameter("status");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String sortBy = request.getParameter("sortBy");
        String sortDir = request.getParameter("sortDir");
        String pageStr = request.getParameter("page");

        // Build ProductSearchDTO
        ProductSearchDTO criteria = new ProductSearchDTO();
        criteria.setKeyword(keyword);
        
        // Parse categoryId
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            try {
                criteria.setCategoryId(Long.parseLong(categoryIdStr));
            } catch (NumberFormatException ignored) {}
        }
        
        // Parse status
        if ("true".equalsIgnoreCase(statusStr)) {
            criteria.setStatus(true);
        } else if ("false".equalsIgnoreCase(statusStr)) {
            criteria.setStatus(false);
        }
        
        // Parse price range
        if (minPriceStr != null && !minPriceStr.isEmpty()) {
            try {
                criteria.setMinPrice(new BigDecimal(minPriceStr));
            } catch (NumberFormatException ignored) {}
        }
        if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
            try {
                criteria.setMaxPrice(new BigDecimal(maxPriceStr));
            } catch (NumberFormatException ignored) {}
        }
        
        // Parse sort
        if (sortBy != null && !sortBy.isEmpty()) {
            criteria.setSortBy(sortBy);
        }
        if (sortDir != null && !sortDir.isEmpty()) {
            criteria.setSortDir(sortDir);
        }
        
        // Parse page (0-based)
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                int page = Integer.parseInt(pageStr);
                criteria.setPage(Math.max(0, page));
            } catch (NumberFormatException ignored) {}
        }

        // Gọi Service để lấy danh sách + đếm tổng
        List<Product> products = productService.searchProducts(criteria);
        long total = productService.countProducts(criteria);
        long totalPages = (total + criteria.getSize() - 1) / criteria.getSize();

        // Set attributes cho JSP
        request.setAttribute("products", products);
        request.setAttribute("categories", categoryService.getAllCategories());
        request.setAttribute("keyword", keyword == null ? "" : keyword);
        request.setAttribute("selectedCategory", criteria.getCategoryId() == null ? 0L : criteria.getCategoryId());
        request.setAttribute("page", criteria.getPage());
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("total", total);

        // Forward tới JSP (điều chỉnh theo thư mục hiện có /view)
        request.getRequestDispatcher("/view/product.list.jsp").forward(request, response);
    }

    /**
     * Hiển thị form tạo/sửa sản phẩm
     * Nếu có "formData" (ProductFormDTO) trong request thì dùng để điền lại form khi có lỗi validate
     */
    private void showForm(HttpServletRequest request, HttpServletResponse response, Product product)
            throws ServletException, IOException {

        // Nếu product được truyền vào (sửa) -> dùng luôn
        if (product != null) {
            request.setAttribute("product", product);
            if (product.getDetail() != null) {
                request.setAttribute("detail", product.getDetail());
            }
        } else {
            // Nếu có formData (trường hợp lỗi validate) -> build Product tạm để hiển thị lại form
            Object formData = request.getAttribute("formData");
            if (formData != null && formData instanceof dto.ProductFormDTO) {
                ProductFormDTO dto = (ProductFormDTO) formData;
                Product p = new Product();
                if (dto.getProductId() != null) p.setId(dto.getProductId());
                p.setSku(dto.getSku());
                p.setName(dto.getName());
                p.setPrice(dto.getPrice());
                p.setQuantity(dto.getQuantity());
                p.setDescription(dto.getDescription());
                if (dto.getCategoryId() != null) {
                    Category c = new Category();
                    c.setId(dto.getCategoryId());
                    p.setCategory(c);
                }
                p.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
                request.setAttribute("product", p);
            } else {
                // Tạo product rỗng để tránh NPE trong JSP
                request.setAttribute("product", new Product());
            }
        }

        // Set danh sách categories để select trong form
        request.setAttribute("categories", categoryService.getAllCategories());

        // Forward tới form JSP (tương thích với file hiện có trong /view)
        request.getRequestDispatcher("/view/product.form.jsp").forward(request, response);
    }

    /**
     * Lưu sản phẩm (tạo hoặc cập nhật)
     */
    private void saveProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lấy dữ liệu từ form
        String productIdStr = request.getParameter("productId");
        String sku = request.getParameter("sku");
        String name = request.getParameter("name");
        String priceStr = request.getParameter("price");
        String quantityStr = request.getParameter("quantity");
        String description = request.getParameter("description");
        String categoryIdStr = request.getParameter("categoryId");
        String statusStr = request.getParameter("status");
        
        String manufacturer = request.getParameter("manufacturer");
        String warrantyStr = request.getParameter("warrantyMonths");
        String origin = request.getParameter("origin");
        String detailDescription = request.getParameter("detailDescription");
        String technicalSpec = request.getParameter("technicalSpec");

        // Build DTO
        ProductFormDTO formDTO = new ProductFormDTO();
        
        if (productIdStr != null && !productIdStr.isEmpty()) {
            formDTO.setProductId(Long.parseLong(productIdStr));
        }
        
        formDTO.setSku(sku);
        formDTO.setName(name);
        formDTO.setPrice(new BigDecimal(priceStr));
        formDTO.setQuantity(Integer.parseInt(quantityStr));
        formDTO.setDescription(description);
        formDTO.setCategoryId(Long.parseLong(categoryIdStr));
        formDTO.setStatus("on".equals(statusStr));
        
        formDTO.setManufacturer(manufacturer);
        formDTO.setWarrantyMonths(Integer.parseInt(warrantyStr));
        formDTO.setOrigin(origin);
        formDTO.setDetailDescription(detailDescription);
        formDTO.setTechnicalSpec(technicalSpec);

        // Gọi Service để lưu
        try {
            if (formDTO.getProductId() != null) {
                // Cập nhật
                productService.updateProduct(formDTO);
            } else {
                // Tạo mới
                productService.createProduct(formDTO);
            }
            
            // Chuyển hướng tới danh sách
            response.sendRedirect(request.getContextPath() + "/products");
        } catch (RuntimeException e) {
            // Lỗi validation - hiển thị form lại với lỗi
            request.setAttribute("error", e.getMessage());
            request.setAttribute("formData", formDTO);
            request.setAttribute("categories", categoryService.getAllCategories());
            request.getRequestDispatcher("/view/product.form.jsp").forward(request, response);
        }
    }
}

