<%@ page import="java.util.List" %>
<%@ page import="entity.Product" %>
<%@ page import="entity.Category" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% List<Product> products = (List<Product>) request.getAttribute("products"); %>
<% java.util.List categories = (java.util.List) request.getAttribute("categories"); %>
<% String keyword = request.getAttribute("keyword") == null ? "" : (String) request.getAttribute("keyword"); %>
<% Long selectedCategory = request.getAttribute("selectedCategory") == null ? 0L : (Long) request.getAttribute("selectedCategory"); %>
<% int currentPage = request.getAttribute("page") == null ? 0 : (Integer) request.getAttribute("page"); %>
<% int totalPages = request.getAttribute("totalPages") == null ? 1 : (Integer) request.getAttribute("totalPages"); %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><title>Danh sach san pham</title></head>
<body>
<h1>Danh sach san pham</h1>
<p><a href="<%= request.getContextPath() %>/products?action=new">Them san pham</a></p>
<form method="get" action="<%= request.getContextPath() %>/products">
    Tu khoa: <input type="text" name="keyword" value="<%= keyword %>">
    Danh muc:
    <select name="categoryId">
        <option value="">-- Tat ca --</option>
        <% if (categories != null) { for (Object o : categories) { Category c = (Category) o; %>
            <option value="<%= c.getId() %>" <%= c.getId() != null && c.getId().equals(selectedCategory) ? "selected" : "" %>><%= c.getName() %></option>
        <% } } %>
    </select>
    <button type="submit">Loc</button>
</form>
<table border="1" cellpadding="8">
    <tr><th>ID</th><th>Ten</th><th>Gia</th><th>So luong</th><th>Danh muc</th><th>Trang thai</th><th>Thao tac</th></tr>
    <% if (products != null) { for (Product product : products) { %>
    <tr>
        <td><%= product.getId() %></td>
        <td><%= product.getName() %></td>
        <td><%= product.getPrice() %></td>
        <td><%= product.getQuatity() %></td>
        <td><%= product.getCategoryName() == null ? "-" : product.getCategoryName() %></td>
        <td><%= product.isStatus() ? "Dang ban" : "Khong ban" %></td>
        <td>
            <a href="<%= request.getContextPath() %>/products?action=edit&id=<%= product.getId() %>">Sua</a>
            <form method="post" action="<%= request.getContextPath() %>/products" style="display:inline">
                <input type="hidden" name="action" value="delete" />
                <input type="hidden" name="id" value="<%= product.getId() %>" />
                <button type="submit" onclick="return confirm('Xóa sản phẩm này?')">Xóa</button>
            </form>
        </td>
    </tr>
    <% } } %>
</table>
<div>
    <% if (totalPages > 1) { %>
        <% int prevPage = Math.max(0, currentPage - 1); %>
        <a href="<%= request.getContextPath() %>/products?keyword=<%= java.net.URLEncoder.encode(keyword, "UTF-8") %>&categoryId=<%= selectedCategory %>&page=<%= prevPage %>">Prev</a>
        <% for (int i = 1; i <= totalPages; i++) {
               int pageParam = i - 1;
               if (pageParam == currentPage) { %>
                   <strong><%= i %></strong>
               <% } else { %>
                   <a href="<%= request.getContextPath() %>/products?keyword=<%= java.net.URLEncoder.encode(keyword, "UTF-8") %>&categoryId=<%= selectedCategory %>&page=<%= pageParam %>"><%= i %></a>
               <% }
           } %>
        <% int nextPage = Math.min(totalPages - 1, currentPage + 1); %>
        <a href="<%= request.getContextPath() %>/products?keyword=<%= java.net.URLEncoder.encode(keyword, "UTF-8") %>&categoryId=<%= selectedCategory %>&page=<%= nextPage %>">Next</a>
    <% } %>
</div>
</body>
</html>
