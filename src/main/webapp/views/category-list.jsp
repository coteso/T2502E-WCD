<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý Danh mục</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Quản lý Danh mục (Category)</h2>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-primary">Sang Quản lý Sản phẩm</a>
    </div>

    <div class="row">
        <!-- Form thêm danh mục -->
        <div class="col-md-4 mb-4">
            <div class="card">
                <div class="card-header bg-primary text-white">Thêm Danh mục Mới</div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/categories" method="post">
                        <input type="hidden" name="action" value="save">
                        <div class="mb-3">
                            <label class="form-label">Tên danh mục (*)</label>
                            <input type="text" name="name" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Mô tả</label>
                            <textarea name="description" class="form-control" rows="3"></textarea>
                        </div>
                        <div class="form-check mb-3">
                            <input class="form-check-input" type="checkbox" name="status" id="statusCheck" checked>
                            <label class="form-check-label" for="statusCheck">Kích hoạt</label>
                        </div>
                        <button type="submit" class="btn btn-success w-100">Lưu Danh mục</button>
                    </form>
                </div>
            </div>
        </div>

        <!-- Bảng danh sách danh mục -->
        <div class="col-md-8">
            <table class="table table-bordered table-striped align-middle">
                <thead class="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Tên</th>
                        <th>Mô tả</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="c" items="${categories}">
                        <tr>
                            <td>${c.id}</td>
                            <td class="fw-bold">${c.name}</td>
                            <td>${c.description}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${c.status}"><span class="badge bg-success">Active</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">Disabled</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/categories" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="toggleStatus">
                                    <input type="hidden" name="id" value="${c.id}">
                                    <button type="submit" class="btn btn-sm ${c.status ? 'btn-warning' : 'btn-info'}">
                                        ${c.status ? 'Vô hiệu hóa' : 'Kích hoạt lại'}
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>