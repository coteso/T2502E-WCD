<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lỗi | Product ORM MVC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container container-narrow">
    <div class="card error-card">
        <h1>😕 ${not empty errorTitle ? errorTitle : 'Có lỗi xảy ra'}</h1>
        <p class="error-message">${not empty errorMessage ? errorMessage : requestScope['jakarta.servlet.error.message']}</p>
        <p class="muted">Mã lỗi: ${not empty statusCode ? statusCode : (requestScope['jakarta.servlet.error.status_code'] ne null ? requestScope['jakarta.servlet.error.status_code'] : 500)}</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/products">Về trang sản phẩm</a>
    </div>
</div>
</body>
</html>
