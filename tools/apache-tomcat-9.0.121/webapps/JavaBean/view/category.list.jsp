<%@ page import="java.util.List" %>
<%@ page import="entity.Category" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><title>Danh mục</title></head>
<body>
<h1>Quản lý danh mục</h1>

<% List<Category> categories = (List<Category>) request.getAttribute("categories"); %>

<form method="post" action="<%= request.getContextPath() %>/categories">
    <input type="hidden" name="id" value="">
    <p>Tên: <input name="name" required></p>
    <button type="submit">Thêm</button>
</form>

<table border="1" cellpadding="8">
    <tr><th>ID</th><th>Tên</th><th>Trạng thái</th><th>Hành động</th></tr>
    <% if (categories != null) { for (Category c : categories) { %>
    <tr>
        <td><%= c.getId() %></td>
        <td><%= c.getName() %></td>
        <td><%= c.isStatus() ? "Active" : "Inactive" %></td>
        <td>
            <form method="post" action="<%= request.getContextPath() %>/categories" style="display:inline">
                <input type="hidden" name="id" value="<%= c.getId() %>">
                <input type="text" name="name" value="<%= c.getName() %>">
                <label><input type="checkbox" name="status" <%= c.isStatus() ? "checked" : "" %>> Active</label>
                <button type="submit">Cập nhật</button>
            </form>
        </td>
    </tr>
    <% } } %>
</table>

<p><a href="<%= request.getContextPath() %>/products">Quay lại sản phẩm</a></p>
</body>
</html>