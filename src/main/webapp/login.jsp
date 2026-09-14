<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><title>Dang nhap</title></head>
<body>
<h1>Dang nhap</h1>
<% if (request.getAttribute("error") != null) { %>
<p><%= request.getAttribute("error") %></p>
<% } %>
<form method="post" action="<%= request.getContextPath() %>/login">
    <p>Ten dang nhap: <input name="username" required value="<%= request.getAttribute("username") == null ? "" : request.getAttribute("username") %>"></p>
    <p>Mat khau: <input type="password" name="password" required></p>
    <button type="submit">Dang nhap</button>
</form>
</body>
</html>
