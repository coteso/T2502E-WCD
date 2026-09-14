<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Danh Sách Sản Phẩm</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản Lý Sản Phẩm</h2>
        <a href="${pageContext.request.contextPath}/products/create" class="btn btn-primary">Thêm Mới</a>
    </div>

    <!-- Form Lọc & Tìm kiếm -->
    <form action="${pageContext.request.contextPath}/products" method="get" class="row g-3 mb-4">
        <div class="col-md-4">
            <input type="text" name="keyword" class="form-control" placeholder="Tìm tên, SKU..." value="${param.keyword}">
        </div>
        <div class="col-md-3">
            <select name="categoryId" class="form-select">
                <option value="">Tất cả danh mục</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat.id}" ${param.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-success w-100">Lọc</button>
        </div>
    </form>

    <!-- Bảng Dữ Liệu -->
    <table class="table table-bordered table-hover">
        <thead class="table-dark">
            <tr>
                <th>SKU</th>
                <th>Tên SP</th>
                <th>Danh Mục</th>
                <th>Giá</th>
                <th>Tồn Kho</th>
                <th>Trạng Thái</th>
                <th>Hành Động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="p" items="${products}">
                <tr>
                    <td>${p.sku}</td>
                    <td>${p.name}</td>
                    <td>${p.category.name}</td>
                    <td>${p.price}</td>
                    <td>${p.quantity}</td>
                    <td>
                        <span class="badge bg-${p.status ? 'success' : 'secondary'}">
                            ${p.status ? 'Active' : 'Inactive'}
                        </span>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/products/edit?id=${p.id}" class="btn btn-sm btn-warning">Sửa</a>
                        <form action="${pageContext.request.contextPath}/products/delete" method="post" class="d-inline">
                            <input type="hidden" name="id" value="${p.id}">
                            <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Xác nhận xoá sản phẩm này?')">Xoá</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>