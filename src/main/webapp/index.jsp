<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Tự động chuyển hướng về Servlet danh mục khi truy cập trang chủ
    response.sendRedirect(request.getContextPath() + "/categories");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Trang Chủ - Product Management</title>
</head>
<body>
<p>Đang chuyển hướng đến danh sách danh mục...</p>
</body>
</html>