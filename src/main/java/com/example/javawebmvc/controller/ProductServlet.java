package com.example.javawebmvc.controller;

import com.example.javawebmvc.dto.PageResult;
import com.example.javawebmvc.dto.ProductFormDTO;
import com.example.javawebmvc.dto.ProductSearchDTO;
import com.example.javawebmvc.entity.Category;
import com.example.javawebmvc.entity.Product;
import com.example.javawebmvc.exception.BusinessException;
import com.example.javawebmvc.exception.ValidationException;
import com.example.javawebmvc.service.CategoryService;
import com.example.javawebmvc.service.ProductService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * CONTROLLER cho Product - chỉ điều phối HTTP, nghiệp vụ nằm ở Service.
 *
 * URL mapping (mục 9 của đề):
 *   GET  /products             -> danh sách + tìm kiếm + lọc + sort + phân trang
 *   GET  /products/create      -> hiển thị form thêm (Product + Detail cùng màn hình)
 *   POST /products/create      -> validate + tạo Product + Detail trong 1 transaction
 *   GET  /products/edit?id=1   -> hiển thị form sửa, load Product kèm Detail
 *   POST /products/edit        -> validate + cập nhật Product + Detail
 *   POST /products/delete?id=1 -> xoá mềm Product
 */
@WebServlet(name = "ProductServlet", urlPatterns = {"/products", "/products/*"},
        loadOnStartup = 1)
public class ProductServlet extends HttpServlet {

    private ProductService productService;
    private CategoryService categoryService;

    @Override
    public void init() {
        productService = new ProductService();
        categoryService = new CategoryService();
    }

    // ------------------------------------------------------------------
    // GET: list / create-form / edit-form
    // ------------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String extra = req.getPathInfo(); // null | /create | /edit

        try {
            if ("/create".equals(extra)) {
                showCreateForm(req, resp);
            } else if ("/edit".equals(extra)) {
                showEditForm(req, resp);
            } else {
                listProducts(req, resp);
            }
        } catch (BusinessException e) {
            showError(req, resp, e.getMessage());
        } catch (Exception e) {
            throw new ServletException("Lỗi hệ thống: " + e.getMessage(), e);
        }
    }

    private void listProducts(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ProductSearchDTO criteria = new ProductSearchDTO();
        criteria.setKeyword(req.getParameter("keyword"));
        criteria.setCategoryId(parseLong(req.getParameter("categoryId")));
        criteria.setStatus(req.getParameter("status"));
        criteria.setMinPrice(parseDecimal(req.getParameter("minPrice")));
        criteria.setMaxPrice(parseDecimal(req.getParameter("maxPrice")));
        criteria.setSortBy(req.getParameter("sortBy"));
        criteria.setSortDir(req.getParameter("sortDir"));
        Long pageParam = parseLong(req.getParameter("page"));
        Long sizeParam = parseLong(req.getParameter("size"));
        criteria.setPage(pageParam == null ? 1 : pageParam.intValue());
        criteria.setSize(sizeParam == null ? 5 : sizeParam.intValue());
        criteria.normalize();

        PageResult<Product> pageData = productService.search(criteria);
        List<Category> categories = categoryService.listActive();

        req.setAttribute("pageData", pageData);
        req.setAttribute("criteria", criteria);
        req.setAttribute("listCategories", categories);

        req.getRequestDispatcher("/WEB-INF/views/product-list.jsp").forward(req, resp);
    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("listCategories", categoryService.listActive());
        req.setAttribute("form", new ProductFormDTO());
        req.getRequestDispatcher("/WEB-INF/views/product-form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = parseLong(req.getParameter("id"));
        if (id == null) {
            throw new BusinessException("Thiếu id sản phẩm cần sửa.");
        }
        Product product = productService.getWithDetail(id)
                .orElseThrow(() -> new BusinessException(
                        "Sản phẩm không tồn tại hoặc đã bị xoá (id=" + id + ")."));

        ProductFormDTO form = new ProductFormDTO();
        form.setId(product.getId());
        form.setSku(product.getSku());
        form.setName(product.getName());
        form.setPrice(product.getPrice() != null ? product.getPrice().toPlainString() : "");
        form.setQuantity(product.getQuantity() != null ? String.valueOf(product.getQuantity()) : "");
        form.setStatus(String.valueOf(product.isStatus()));
        form.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);

        if (product.getDetail() != null) {
            form.setManufacturer(product.getDetail().getManufacturer());
            form.setWarrantyMonths(product.getDetail().getWarrantyMonths() != null
                    ? String.valueOf(product.getDetail().getWarrantyMonths()) : "0");
            form.setOrigin(product.getDetail().getOrigin());
            form.setDetailDescription(product.getDetail().getDescription());
            form.setTechnicalSpec(product.getDetail().getTechnicalSpec());
            form.setDetailId(product.getDetail().getId());
        }

        req.setAttribute("form", form);
        req.setAttribute("listCategories", categoryService.listActive());
        req.getRequestDispatcher("/WEB-INF/views/product-form.jsp").forward(req, resp);
    }

    // ------------------------------------------------------------------
    // POST: create / edit / delete
    // ------------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String extra = req.getPathInfo();
        String action = req.getParameter("action"); // dự phòng cho form dùng query action=

        try {
            if ("/create".equals(extra) || "create".equals(action)) {
                create(req, resp);
            } else if ("/edit".equals(extra) || "edit".equals(action)) {
                update(req, resp);
            } else if ("/delete".equals(extra) || "delete".equals(action)) {
                delete(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ValidationException e) {
            // Validate lỗi: forward ngược form, giữ dữ liệu đã nhập + map lỗi theo từng trường.
            req.setAttribute("form", ProductFormDTO.fromRequest(req));
            req.setAttribute("errors", e.getErrors());
            req.setAttribute("listCategories", categoryService.listActive());
            req.getRequestDispatcher("/WEB-INF/views/product-form.jsp").forward(req, resp);
        } catch (BusinessException e) {
            req.setAttribute("form", ProductFormDTO.fromRequest(req));
            req.setAttribute("error", e.getMessage());
            req.setAttribute("listCategories", categoryService.listActive());
            req.getRequestDispatcher("/WEB-INF/views/product-form.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Lỗi hệ thống: " + e.getMessage(), e);
        }
    }

    private void create(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProductFormDTO form = ProductFormDTO.fromRequest(req);
        Product product = productService.create(form);
        req.getSession().setAttribute("flash",
                "Tạo sản phẩm thành công. SKU: " + product.getSku());
        resp.sendRedirect(req.getContextPath() + "/products");
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProductFormDTO form = ProductFormDTO.fromRequest(req);
        productService.update(form);
        req.getSession().setAttribute("flash", "Cập nhật sản phẩm thành công.");
        resp.sendRedirect(req.getContextPath() + "/products");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));
        if (id == null) {
            throw new BusinessException("Thiếu id sản phẩm cần xoá.");
        }
        productService.softDelete(id);
        req.getSession().setAttribute("flash", "Đã xoá mềm sản phẩm (id=" + id + ").");
        resp.sendRedirect(req.getContextPath() + "/products");
    }

    private void showError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("errorTitle", "Có lỗi xảy ra");
        req.setAttribute("errorMessage", message);
        req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
    }

    private Long parseLong(String raw) {
        try {
            return (raw == null || raw.isBlank()) ? null : Long.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseDecimal(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(raw.replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
