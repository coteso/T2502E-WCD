<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách Sản phẩm - ORM JPA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container-fluid py-4 px-5">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>Quản lý Sản phẩm (JPA / Hibernate ORM)</h2>
        <div>
            <a href="${pageContext.request.contextPath}/categories" class="btn btn-outline-info me-2">Quản lý Danh mục</a>
            <a href="${pageContext.request.contextPath}/products/create" class="btn btn-success">+ Thêm Sản Phẩm Mới</a>
        </div>
    </div>

    <!-- Bộ lọc nâng cao -->
    <div class="card mb-4 bg-light shadow-sm">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/products" method="get" class="row g-2 align-items-center">
                <div class="col-md-2">
                    <input type="text" name="keyword" value="${criteria.keyword}" class="form-control form-control-sm" placeholder="Tìm tên hoặc SKU...">
                </div>
                <div class="col-md-2">
                    <select name="categoryId" class="form-select form-select-sm">
                        <option value="">-- Tất cả danh mục --</option>
                        <c:forEach var="c" items="${categories}">
                            <option value="${c.id}" <c:if test="${criteria.categoryId == c.id}">selected</c:if>>${c.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <select name="status" class="form-select form-select-sm">
                        <option value="">-- Trạng thái --</option>
                        <option value="true" <c:if test="${criteria.status == true}">selected</c:if>>Hoạt động</option>
                        <option value="false" <c:if test="${criteria.status == false}">selected</c:if>>Ngừng bán</option>
                    </select>
                </div>
                <div class="col-md-1">
                    <input type="number" step="any" name="minPrice" value="${criteria.minPrice}" class="form-control form-control-sm" placeholder="Giá từ">
                </div>
                <div class="col-md-1">
                    <input type="number" step="any" name="maxPrice" value="${criteria.maxPrice}" class="form-control form-control-sm" placeholder="Giá đến">
                </div>
                <div class="col-md-2">
                    <select name="sortBy" class="form-select form-select-sm">
                        <option value="createdAt" <c:if test="${criteria.sortBy == 'createdAt'}">selected</c:if>>Sắp xếp: Mới nhất</option>
                        <option value="price" <c:if test="${criteria.sortBy == 'price'}">selected</c:if>>Sắp xếp: Theo Giá</option>
                        <option value="name" <c:if test="${criteria.sortBy == 'name'}">selected</c:if>>Sắp xếp: Theo Tên</option>
                    </select>
                </div>
                <div class="col-md-1">
                    <select name="sortDir" class="form-select form-select-sm">
                        <option value="desc" <c:if test="${criteria.sortDir == 'desc'}">selected</c:if>>Giảm dần</option>
                        <option value="asc" <c:if test="${criteria.sortDir == 'asc'}">selected</c:if>>Tăng dần</option>
                    </select>
                </div>
                <div class="col-md-1 d-flex gap-1">
                    <button type="submit" class="btn btn-primary btn-sm w-100">Lọc</button>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary btn-sm">Reset</a>
                </div>
            </form>
        </div>
    </div>

    <!-- Bảng danh sách -->
    <table class="table table-bordered table-striped align-middle shadow-sm">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>SKU</th>
                <th>Tên sản phẩm</th>
                <th>Giá bán</th>
                <th>Tồn kho</th>
                <th>Danh mục</th>
                <th>Trạng thái</th>
                <th style="width: 150px;">Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${not empty products}">
                    <c:forEach var="p" items="${products}">
                        <tr>
                            <td>${p.id}</td>
                            <td><span class="badge bg-secondary">${p.sku}</span></td>
                            <td class="fw-bold">${p.name}</td>
                            <td><fmt:formatNumber value="${p.price}" pattern="#,##0"/> ₫</td>
                            <td>${p.quantity}</td>
                            <td>${p.category.name}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${p.status}"><span class="badge bg-success">Hoạt động</span></c:when>
                                    <c:otherwise><span class="badge bg-danger">Ngừng bán</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/products/edit?id=${p.id}" class="btn btn-sm btn-warning">Sửa</a>
                                <form action="${pageContext.request.contextPath}/products/delete?id=${p.id}" method="post" style="display:inline;">
                                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc chắn muốn xoá mềm sản phẩm này?');">Xóa</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="8" class="text-center text-muted py-4">Không tìm thấy sản phẩm nào!</td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

    <!-- Phân trang -->
    <c:if test="${totalPages > 1}">
        <nav>
            <ul class="pagination justify-content-center">
                <c:if test="${criteria.page > 1}">
                    <li class="page-item">
                        <a class="page-link" href="${pageContext.request.contextPath}/products?page=${criteria.page - 1}&keyword=${criteria.keyword}&categoryId=${criteria.categoryId}&status=${criteria.status}&minPrice=${criteria.minPrice}&maxPrice=${criteria.maxPrice}&sortBy=${criteria.sortBy}&sortDir=${criteria.sortDir}">Trước</a>
                    </li>
                </c:if>
                <li class="page-item disabled"><span class="page-link">Trang ${criteria.page} / ${totalPages}</span></li>
                <c:if test="${criteria.page < totalPages}">
                    <li class="page-item">
                        <a class="page-link" href="${pageContext.request.contextPath}/products?page=${criteria.page + 1}&keyword=${criteria.keyword}&categoryId=${criteria.categoryId}&status=${criteria.status}&minPrice=${criteria.minPrice}&maxPrice=${criteria.maxPrice}&sortBy=${criteria.sortBy}&sortDir=${criteria.sortDir}">Sau</a>
                    </li>
                </c:if>
            </ul>
        </nav>
    </c:if>
</body>
</html>