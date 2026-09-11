package com.example.javawebmvc.controller;

import com.example.javawebmvc.dao.CategoryDAO;
import com.example.javawebmvc.model.Category;
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
 * CONTROLLER - Điều phối toàn bộ thao tác CRUD cho Category (Danh mục).
 * Áp dụng cấu trúc Front Controller thu nhỏ, điều hướng bằng tham số "action".
 * Tuân thủ đúng thiết kế vòng đời Lifecycle và Thread-safe của thầy giáo.
 */
@WebServlet(
        name = "CategoryServlet",
        urlPatterns = {"/categories"},
        loadOnStartup = 1,
        initParams = {
                @WebInitParam(name = "pageTitle", value = "Quản lý danh mục"),
                @WebInitParam(name = "defaultAction", value = "list")
        }
)
public class CategoryServlet extends HttpServlet {

    private static final String VIEW_LIST = "/WEB-INF/views/category/list.jsp";
    private static final String VIEW_FORM = "/WEB-INF/views/category/form.jsp";

    /** Tài nguyên dùng chung, tạo 1 lần trong init() và dùng lại cho mọi request */
    private CategoryDAO categoryDAO;

    /** Các tham số cấu hình đọc từ ServletConfig */
    private String pageTitle;
    private String defaultAction;

    /** Bộ đếm dùng chung nhiều thread -> bắt buộc dùng loại thread-safe */
    private final AtomicLong requestCount = new AtomicLong();

    // ---------------------------------------------------------------------
    // (1) LOAD & INSTANTIATE
    // ---------------------------------------------------------------------
    public CategoryServlet() {
        super();
        System.out.println("[LIFECYCLE] CategoryServlet (1) CONSTRUCTOR - instance cho danh mục vừa được tạo");
    }

    // ---------------------------------------------------------------------
    // (2) INIT - Chạy ĐÚNG 1 LẦN khi deploy ứng dụng
    // ---------------------------------------------------------------------
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log("[LIFECYCLE] (2a) init(ServletConfig) - category servlet name = " + config.getServletName());
    }

    @Override
    public void init() throws ServletException {
        this.categoryDAO = new CategoryDAO();
        this.pageTitle = getInitParameter("pageTitle");
        this.defaultAction = getInitParameter("defaultAction");

        log("[LIFECYCLE] (2b) init() - Đã tạo CategoryDAO thành công | pageTitle = " + pageTitle);
    }

    // ---------------------------------------------------------------------
    // (3) SERVICE - Điều phối đa luồng (Multi-thread)
    // ---------------------------------------------------------------------
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long n = requestCount.incrementAndGet();
        log("[LIFECYCLE] (3) service() Category - " + req.getMethod() + " " + req.getRequestURI()
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
        log("[LIFECYCLE] (3a) doGet() Category - action = " + action);

        try {
            switch (action) {
                case "new":
                    showForm(req, resp, null);
                    break;
                case "edit":
                    int idToEdit = parseId(req);
                    // Cần đảm bảo CategoryDAO đã có hàm findById, nếu chưa có hãy dùng tạm hàm lấy theo logic của bạn
                    Category existingCategory = categoryDAO.findAll().stream()
                            .filter(c -> c.getId() == idToEdit)
                            .findFirst()
                            .orElse(null);
                    showForm(req, resp, existingCategory);
                    break;
                case "delete":
                    categoryDAO.delete(parseId(req));
                    req.getSession().setAttribute("message", "Đã xóa danh mục thành công.");
                    resp.sendRedirect(req.getContextPath() + "/categories");
                    break;
                case "list":
                default:
                    listCategories(req, resp);
                    break;
            }
        } catch (Exception e) {
            req.setAttribute("error", "Đã xảy ra lỗi hệ thống danh mục: " + e.getMessage());
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
        log("[LIFECYCLE] (3b) doPost() Category - action = " + action);

        try {
            switch (action) {
                case "insert":
                    Category newCategory = buildCategoryFromRequest(req, false);
                    categoryDAO.insert(newCategory);
                    req.getSession().setAttribute("message", "Thêm danh mục thành công.");
                    resp.sendRedirect(req.getContextPath() + "/categories");
                    break;
                case "update":
                    Category updateCategory = buildCategoryFromRequest(req, true);
                    categoryDAO.update(updateCategory);
                    req.getSession().setAttribute("message", "Cập nhật danh mục thành công.");
                    resp.sendRedirect(req.getContextPath() + "/categories");
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/categories");
                    break;
            }
        } catch (Exception e) {
            req.setAttribute("error", "Lỗi xử lý dữ liệu form danh mục: " + e.getMessage());
            showForm(req, resp, buildCategoryFromRequest(req, "update".equals(action)));
        }
    }

    // ---------------------------------------------------------------------
    // CÁC HÀM ĐIỀU PHỐI BỔ TRỢ (HELPER METHODS)
    // ---------------------------------------------------------------------

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Category category)
            throws ServletException, IOException {
        req.setAttribute("category", category);
        req.getRequestDispatcher(VIEW_FORM).forward(req, resp);
    }

    private void listCategories(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Category> list = categoryDAO.findAll();
        req.setAttribute("listCategories", list);
        req.getRequestDispatcher(VIEW_LIST).forward(req, resp);
    }

    private int parseId(HttpServletRequest req) {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            throw new IllegalArgumentException("ID danh mục không hợp lệ.");
        }
        return Integer.parseInt(idStr.trim());
    }

    private Category buildCategoryFromRequest(HttpServletRequest req, boolean isUpdate) {
        Category c = new Category();

        if (isUpdate) {
            c.setId(parseId(req));
            String statusStr = req.getParameter("status");
            c.setStatus(statusStr != null && Boolean.parseBoolean(statusStr));
        } else {
            c.setId(0);
            c.setStatus(true); // Tạo mới danh mục mặc định trạng thái hoạt động (true)
        }

        c.setName(req.getParameter("name"));

        return c;
    }

    // ---------------------------------------------------------------------
    // (4) DESTROY - Chạy 1 lần duy nhất khi tắt Server Tomcat
    // ---------------------------------------------------------------------
    @Override
    public void destroy() {
        log("[LIFECYCLE] (4) destroy() - Giải phóng tài nguyên CategoryServlet hoàn tất");
        super.destroy();
    }
}
