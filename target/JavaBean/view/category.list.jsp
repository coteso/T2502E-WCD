<%@ page import="java.util.List" %>
<%@ page import="entity.Category" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh mục</title>
</head>
<body class="container">
<header class="site-header">
    <h1><a href="<%= request.getContextPath() %>/products">Product Admin</a></h1>
    <nav>
        <a href="<%= request.getContextPath() %>/products">Products</a>
        <a href="<%= request.getContextPath() %>/categories">Categories</a>
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </nav>
</header>
<div class="card">
<h1>Quản lý danh mục</h1>

<% List<Category> categories = (List<Category>) request.getAttribute("categories"); %>

<form method="post" action="<%= request.getContextPath() %>/categories">
    <input type="hidden" name="id" value="">
    <p>Tên: <input name="name" required></p>
    <button type="submit">Thêm</button>
</form>

<table class="table" cellpadding="8">
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

<p><a href="<%= request.getContextPath() %>/products" class="btn secondary">Quay lại sản phẩm</a></p>
</div>
</body>
</html>