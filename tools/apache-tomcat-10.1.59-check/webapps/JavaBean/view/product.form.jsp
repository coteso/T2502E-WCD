<%@ page import="model.Product" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% Product product = (Product) request.getAttribute("product"); %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><title>San pham</title></head>
<body>
<h1><%= product.getId() == 0 ? "Them san pham" : "Sua san pham" %></h1>
<form method="post" action="<%= request.getContextPath() %>/products">
	<input type="hidden" name="id" value="<%= product.getId() == 0 ? "" : product.getId() %>">
	<p>Ten: <input name="name" required value="<%= product.getName() == null ? "" : product.getName() %>"></p>
	<p>Gia: <input type="number" name="price" step="0.01" min="0" required value="<%= product.getPrice() %>"></p>
	<p>So luong: <input type="number" name="quantity" min="0" required value="<%= product.getQuatity() %>"></p>
	<p>Mo ta: <input name="description" value="<%= product.getDescription() == null ? "" : product.getDescription() %>"></p>
	<button type="submit">Luu</button>
	<a href="<%= request.getContextPath() %>/products">Huy</a>
</form>
</body>
</html>
