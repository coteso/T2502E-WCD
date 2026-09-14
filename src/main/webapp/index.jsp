<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Trang chủ | Product ORM MVC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container container-narrow">
    <div class="card home-card">
        <h1>📦 Product ORM MVC</h1>
        <p>Ứng dụng quản lý sản phẩm theo kiến trúc MVC nhiều lớp với JPA/Hibernate:</p>
        <ul class="home-list">
            <li>Controller (Servlet) → Service → Repository → Entity (JPA)</li>
            <li>Category 1-n Product · Product 1-1 ProductDetail</li>
            <li>Tìm kiếm, lọc, sắp xếp, phân trang bằng Criteria API</li>
            <li>Soft delete, validate nghiệp vụ, transaction hoàn chỉnh</li>
        </ul>
        <div class="home-links">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/products">Quản lý sản phẩm</a>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/categories">Quản lý danh mục</a>
        </div>
    </div>
</div>
</body>
</html>
