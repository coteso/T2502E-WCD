<%@ page import="entity.Product" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% Product product = (Product) request.getAttribute("product"); %>
<% java.util.Map<String,String> errors = (java.util.Map<String,String>) request.getAttribute("errors"); %>
<% java.util.List categories = (java.util.List) request.getAttribute("categories"); %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><title>San pham</title></head>
<body>
<h1><%= (product != null && product.getId() != null) ? "Sửa sản phẩm" : "Thêm sản phẩm" %></h1>
<form method="post" action="<%= request.getContextPath() %>/products">
    <input type="hidden" name="productId" value="<%= (product != null && product.getId() != null) ? product.getId() : "" %>">
    <p>SKU: <input name="sku" required value="<%= product != null && product.getSku() != null ? product.getSku() : "" %>"></p>
    <p>Tên: <input name="name" required value="<%= product != null && product.getName() != null ? product.getName() : "" %>"></p>
    <% if (errors != null && errors.get("name") != null) { %>
        <p style="color:red;"><%= errors.get("name") %></p>
    <% } %>
    <p>Giá: <input type="number" name="price" step="0.01" min="0" required value="<%= product != null && product.getPrice() != null ? product.getPrice() : "" %>"></p>
    <% if (errors != null && errors.get("price") != null) { %>
        <p style="color:red;"><%= errors.get("price") %></p>
    <% } %>
    <p>Số lượng: <input type="number" name="quantity" min="0" required value="<%= product != null ? product.getQuatity() : 0 %>"></p>
    <% if (errors != null && errors.get("quantity") != null) { %>
        <p style="color:red;"><%= errors.get("quantity") %></p>
    <% } %>
    <p>Danh mục:
        <select name="categoryId" required>
            <option value="">-- Chọn danh mục --</option>
        <% java.util.List cats = (java.util.List) request.getAttribute("categories");
            if (cats != null) {
                for (Object o : cats) { entity.Category c = (entity.Category) o; %>
                    <option value="<%= c.getId()%>" <%= (product != null && product.getCategory() != null && product.getCategory().getId() != null && product.getCategory().getId().equals(c.getId())) ? "selected" : "" %>><%= c.getName()%></option>
                <% }
            }
        %>
        </select>
    </p>
    <% if (errors != null && errors.get("category") != null) { %>
        <p style="color:red;"><%= errors.get("category") %></p>
    <% } %>
    <p>Trạng thái: <input type="checkbox" name="status" <%= (product != null && product.isStatus()) ? "checked" : "" %> > Đang bán</p>
    <p>Mô tả: <textarea name="description"><%= product != null && product.getDescription() != null ? product.getDescription() : "" %></textarea></p>

    <h3>Chi tiết sản phẩm</h3>
    <p>Nhà sản xuất: <input name="manufacturer" required value="<%= request.getAttribute("detail") != null ? ((entity.ProductDetail)request.getAttribute("detail")).getManufacturer() : "" %>"></p>
    <p>Bảo hành (tháng): <input type="number" name="warrantyMonths" min="0" required value="<%= request.getAttribute("detail") != null ? ((entity.ProductDetail)request.getAttribute("detail")).getWarrantyMonths() : 0 %>"></p>
    <p>Xuất xứ: <input name="origin" required value="<%= request.getAttribute("detail") != null ? ((entity.ProductDetail)request.getAttribute("detail")).getOrigin() : "" %>"></p>
    <p>Mô tả chi tiết: <textarea name="detailDescription"><%= request.getAttribute("detail") != null ? ((entity.ProductDetail)request.getAttribute("detail")).getDescription() : "" %></textarea></p>
    <p>Thông số kỹ thuật: <textarea name="technicalSpec"><%= request.getAttribute("detail") != null ? ((entity.ProductDetail)request.getAttribute("detail")).getTechnicalSpec() : "" %></textarea></p>

    <button type="submit">Lưu</button>
    <a href="<%= request.getContextPath() %>/products">Hủy</a>
</form>
</body>
</html>
