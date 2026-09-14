<%@ page import="java.util.List" %>
<%@ page import="model.Product" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% List<Product> products = (List<Product>) request.getAttribute("products"); %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><title>Danh sach san pham</title></head>
<body>
<h1>Danh sach san pham</h1>
<p><a href="<%= request.getContextPath() %>/products?action=new">Them san pham</a></p>
<table border="1" cellpadding="8">
	<tr><th>ID</th><th>Ten</th><th>Gia</th><th>So luong</th><th>Trang thai</th><th>Thao tac</th></tr>
	<% for (Product product : products) { %>
	<tr>
		<td><%= product.getId() %></td>
		<td><%= product.getName() %></td>
		<td><%= product.getPrice() %></td>
		<td><%= product.getQuatity() %></td>
		<td><%= product.getDescription() == null ? "Dang ban" : product.getDescription() %></td>
		<td>
			<a href="<%= request.getContextPath() %>/products?action=edit&id=<%= product.getId() %>">Sua</a>
			<a href="<%= request.getContextPath() %>/products?action=delete&id=<%= product.getId() %>" onclick="return confirm('Xoa san pham nay?')">Xoa</a>
		</td>
	</tr>
	<% } %>
</table>
</body>
</html>
