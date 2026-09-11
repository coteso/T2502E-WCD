package com.example.javawebmvc.controller;

import com.example.javawebmvc.dao.CategoryDAO;
import com.example.javawebmvc.dao.ProductDAO;
import com.example.javawebmvc.model.Category;
import com.example.javawebmvc.model.Product;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CONTROLLER - Điều phối toàn bộ thao tác CRUD cho Product theo mẫu của thầy giáo.
 * Áp dụng cấu trúc Front Controller thu nhỏ, điều hướng bằng tham số "action".
 * Đã cấu hình tương thích kiểu dữ liệu Double cho Price và thuộc tính của dự án mới.
 */
@WebServlet(
        name = "ProductServlet",
        urlPatterns = {"/products"},
        loadOnStartup = 1,
        initParams = {
                @WebInitParam(name = "pageTitle", value = "Quản lý sản phẩm"),
                @WebInitParam(name = "defaultAction", value = "list")
        }
)
public class ProductServlet extends HttpServlet {

    private static final String VIEW_LIST = "/WEB-INF/views/product/list.jsp";
    private static final String VIEW_FORM = "/WEB-INF/views/product/form.jsp";

    /** Tài nguyên dùng chung, tạo 1 lần trong init() và dùng lại cho mọi request */
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    /** Các tham số cấu hình đọc từ ServletConfig */
    private String pageTitle;
    private String defaultAction;

    /** Bộ đếm dùng chung nhiều thread -> bắt buộc dùng loại thread-safe */
    private final AtomicLong requestCount = new AtomicLong();

    // ---------------------------------------------------------------------
    // (1) LOAD & INSTANTIATE
    // ---------------------------------------------------------------------
    public ProductServlet() {
        super();
        System.out.println("[LIFECYCLE] ProductServlet (1) CONSTRUCTOR - instance vừa được tạo");
    }

    // ---------------------------------------------------------------------
    // (2) INIT - Chạy ĐÚNG 1 LẦN khi deploy ứng dụng
    // ---------------------------------------------------------------------
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log("[LIFECYCLE] (2a) init(ServletConfig) - servlet name = " + config.getServletName());
    }

    @Override
    public void init() throws ServletException {
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO(); // Khởi tạo thêm CategoryDAO để lấy danh mục cho Form
        this.pageTitle = getInitParameter("pageTitle");
        this.defaultAction = getInitParameter("defaultAction");

        // Cấp phạm vi TOÁN ỨNG DỤNG (ServletContext) để hiển thị tiêu đề trang
        getServletContext().setAttribute("appPageTitle", pageTitle);

        log("[LIFECYCLE] (2b) init() - Đã tạo DAO thành công | pageTitle = " + pageTitle);
    }

    // ---------------------------------------------------------------------
    // (3) SERVICE - Chạy cho MỖI request, điều phối đa luồng (Multi-thread)
    // ---------------------------------------------------------------------
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long n = requestCount.incrementAndGet();
        log("[LIFECYCLE] (3) service() - " + req.getMethod() + " " + req.getRequestURI()
                + " | request thứ " + n + " | thread = " + Thread.currentThread().getName());
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = defaultAction;
        }
        log("[LIFECYCLE] (3a) doGet() - action = " + action);

        try {
            switch (action) {
                case "new":
                    showForm(req, resp, null);
                    break;
                case "edit":
                    int idToEdit = parseId(req);
                    Product existingProduct = productDAO.findById(idToEdit); // Cần đảm bảo ProductDAO đã viết hàm findById
                    showForm(req, resp, existingProduct);
                    break;
                case "delete":
                    productDAO.delete(parseId(req));
                    req.getSession().setAttribute("message", "Đã xóa sản phẩm thành công.");
                    resp.sendRedirect(req.getContextPath() + "/products");
                    break;
                case "list":
                default:
                    listProducts(req, resp);
                    break;
            }
        } catch (Exception e) {
            req.setAttribute("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            req.getRequestDispatcher(VIEW_LIST).forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }
        log("[LIFECYCLE] (3b) doPost() - action = " + action);

        try {
            switch (action) {
                case "insert":
                    Product newProduct = buildProductFromRequest(req, false);
                    productDAO.insert(newProduct);
                    req.getSession().setAttribute("message", "Thêm sản phẩm thành công.");
                    resp.sendRedirect(req.getContextPath() + "/products");
                    break;
                case "update":
                    Product updateProduct = buildProductFromRequest(req, true);
                    productDAO.update(updateProduct); // Cần đảm bảo ProductDAO đã viết hàm update
                    req.getSession().setAttribute("message", "Cập nhật sản phẩm thành công.");
                    resp.sendRedirect(req.getContextPath() + "/products");
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/products");
                    break;
            }
        } catch (Exception e) {
            req.setAttribute("error", "Lỗi xử lý dữ liệu form: " + e.getMessage());
            showForm(req, resp, buildProductFromRequest(req, "update".equals(action)));
        }
    }

    // ---------------------------------------------------------------------
    // CÁC HÀM DIEU PHỐI BỔ TRỢ (HELPER METHODS)
    // ---------------------------------------------------------------------

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Product product)
            throws ServletException, IOException {
        req.setAttribute("product", product);

        // Đọc thêm danh sách danh mục để đổ vào ô <select> combobox trên Form
        List<Category> listCategories = categoryDAO.findAll();
        req.setAttribute("listCategories", listCategories);

        req.getRequestDispatcher(VIEW_FORM).forward(req, resp);
    }

    private void listProducts(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Product> list = productDAO.findAll();
        req.setAttribute("listProducts", list);
        req.getRequestDispatcher(VIEW_LIST).forward(req, resp);
    }

    private int parseId(HttpServletRequest req) {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ.");
        }
        return Integer.parseInt(idStr.trim());
    }

    private Product buildProductFromRequest(HttpServletRequest req, boolean isUpdate) {
        Product p = new Product();

        if (isUpdate) {
            p.setId(parseId(req));
            String statusStr = req.getParameter("status");
            p.setStatus(statusStr != null && Boolean.parseBoolean(statusStr));
        } else {
            p.setId(0);
            p.setStatus(true); // Tạo mới mặc định hoạt động
        }

        p.setName(req.getParameter("name"));

        // Đổi từ cấu trúc BigDecimal cũ của thầy sang kiểu double của bạn
        String priceStr = req.getParameter("price");
        p.setPrice(priceStr != null && !priceStr.isEmpty() ? Double.parseDouble(priceStr) : 0.0);

        String quantityStr = req.getParameter("quantity");
        p.setQuantity(quantityStr != null && !quantityStr.isEmpty() ? Integer.parseInt(quantityStr) : 0);

        String categoryIdStr = req.getParameter("categoryId");
        p.setCategoryId(categoryIdStr != null && !categoryIdStr.isEmpty() ? Integer.parseInt(categoryIdStr) : 0);

        p.setDeleted(false); // Mặc định chưa bị xóa mềm

        return p;
    }

    // ---------------------------------------------------------------------
    // (4) DESTROY - Chạy 1 lần duy nhất khi tắt Server Tomcat
    // ---------------------------------------------------------------------
    @Override
    public void destroy() {
        log("[LIFECYCLE] (4) destroy() - Giải phóng tài nguyên ProductServlet hoàn tất");
        super.destroy();
    }
}

