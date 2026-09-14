<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Danh Mục Sản Phẩm</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản Lý Danh Mục</h2>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Về Trang Sản Phẩm</a>
    </div>

    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger">${sessionScope.error}</div>
        <c:remove var="error" scope="session" />
    </c:if>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success">${sessionScope.message}</div>
        <c:remove var="message" scope="session" />
    </c:if>

    <table class="table table-bordered">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Tên Danh Mục</th>
                <th>Trạng Thái</th>
                <th>Hành Động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="cat" items="${categories}">
                <tr>
                    <td>${cat.id}</td>
                    <td>${cat.name}</td>
                    <td>
                        <span class="badge bg-${cat.status ? 'success' : 'secondary'}">
                            ${cat.status ? 'Active' : 'Inactive'}
                        </span>
                    </td>
                    <td>
                        <c:if test="${cat.status}">
                            <form action="${pageContext.request.contextPath}/categories" method="post" class="d-inline">
                                <input type="hidden" name="action" value="disable">
                                <input type="hidden" name="id" value="${cat.id}">
                                <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Vô hiệu hoá danh mục này?')">Vô Hiệu</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>