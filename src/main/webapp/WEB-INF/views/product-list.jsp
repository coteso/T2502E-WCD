<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách sản phẩm | Product ORM MVC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">

    <header class="page-header">
        <h1>Quản lý sản phẩm</h1>
        <nav>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/categories">Danh mục</a>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/products/create">+ Thêm sản phẩm</a>
        </nav>
    </header>

    <c:if test="${not empty flash}">
        <div class="alert alert-success">${flash}</div>
        <c:remove var="flash" scope="session"/>
    </c:if>
    <c:if test="${not empty flashError}">
        <div class="alert alert-error">${flashError}</div>
        <c:remove var="flashError" scope="session"/>
    </c:if>

    <%-- Bộ lọc: keyword, category, status, khoảng giá, sort --%>
    <form method="get" action="${pageContext.request.contextPath}/products" class="filter-bar">
        <input type="text" name="keyword" placeholder="Tìm theo tên hoặc SKU..."
               value="${criteria.keyword}">

        <select name="categoryId">
            <option value="">-- Tất cả danh mục --</option>
            <c:forEach var="cat" items="${listCategories}">
                <option value="${cat.id}" ${criteria.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
            </c:forEach>
        </select>

        <select name="status">
            <option value="">-- Mọi trạng thái --</option>
            <option value="active" ${criteria.status == 'active' ? 'selected' : ''}>Đang bán</option>
            <option value="inactive" ${criteria.status == 'inactive' ? 'selected' : ''}>Ngừng bán</option>
        </select>

        <input type="text" name="minPrice" placeholder="Giá từ..." value="${criteria.minPrice}" size="8">
        <input type="text" name="maxPrice" placeholder="Đến giá..." value="${criteria.maxPrice}" size="8">

        <select name="sortBy">
            <option value="createdAt" ${criteria.sortBy == 'createdAt' ? 'selected' : ''}>Sắp xếp: Ngày tạo</option>
            <option value="name" ${criteria.sortBy == 'name' ? 'selected' : ''}>Sắp xếp: Tên</option>
            <option value="price" ${criteria.sortBy == 'price' ? 'selected' : ''}>Sắp xếp: Giá</option>
        </select>

        <select name="sortDir">
            <option value="desc" ${criteria.sortDir == 'desc' ? 'selected' : ''}>Giảm dần</option>
            <option value="asc" ${criteria.sortDir == 'asc' ? 'selected' : ''}>Tăng dần</option>
        </select>

        <button type="submit" class="btn btn-primary">Lọc / Tìm</button>
        <a class="btn btn-outline" href="${pageContext.request.contextPath}/products">Xoá lọc</a>
    </form>

    <table class="table">
        <thead>
        <tr>
            <th>ID</th>
            <th>SKU</th>
            <th>Tên sản phẩm</th>
            <th>Danh mục</th>
            <th>Giá</th>
            <th>Tồn kho</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${not empty pageData.items}">
                <c:forEach var="p" items="${pageData.items}">
                    <tr>
                        <td>${p.id}</td>
                        <td><code>${p.sku}</code></td>
                        <td>${p.name}</td>
                        <td>${p.categoryName}</td>
                        <td class="text-right"><fmt:formatNumber value="${p.price}" pattern="#,##0.00"/> đ</td>
                        <td class="text-right">${p.quantity}</td>
                        <td>
                            <span class="badge ${p.status ? 'badge-active' : 'badge-inactive'}">
                                ${p.status ? 'Đang bán' : 'Ngừng bán'}
                            </span>
                        </td>
                        <td>
                            <a class="btn btn-sm btn-warning"
                               href="${pageContext.request.contextPath}/products/edit?id=${p.id}">Sửa</a>
                            <%-- Soft delete bắt buộc POST (mục 9) --%>
                            <form method="post" style="display:inline"
                                  action="${pageContext.request.contextPath}/products/delete?id=${p.id}"
                                  onsubmit="return confirm('Xoá mềm sản phẩm này? (có thể khôi phục trong DB)');">
                                <button type="submit" class="btn btn-sm btn-danger">Xoá</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr><td colspan="8" class="empty-row">Không có sản phẩm nào khớp điều kiện.</td></tr>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>

    <%-- Phân trang: giữ nguyên bộ lọc khi chuyển trang --%>
    <c:if test="${pageData.totalPages > 1}">
        <div class="pagination">
            <c:url var="pageUrl" value="/products">
                <c:param name="keyword" value="${criteria.keyword}"/>
                <c:param name="categoryId" value="${criteria.categoryId}"/>
                <c:param name="status" value="${criteria.status}"/>
                <c:param name="minPrice" value="${criteria.minPrice}"/>
                <c:param name="maxPrice" value="${criteria.maxPrice}"/>
                <c:param name="sortBy" value="${criteria.sortBy}"/>
                <c:param name="sortDir" value="${criteria.sortDir}"/>
                <c:param name="size" value="${criteria.size}"/>
            </c:url>

            <c:if test="${pageData.hasPrevious}">
                <a class="page-link" href="${pageUrl}&page=${pageData.page - 1}">&laquo; Trước</a>
            </c:if>

            <c:forEach begin="1" end="${pageData.totalPages}" var="pg">
                <a class="page-link ${pg == pageData.page ? 'page-active' : ''}"
                   href="${pageUrl}&page=${pg}">${pg}</a>
            </c:forEach>

            <c:if test="${pageData.hasNext}">
                <a class="page-link" href="${pageUrl}&page=${pageData.page + 1}">Sau &raquo;</a>
            </c:if>
        </div>
    </c:if>

    <p class="muted">Tổng: <strong>${pageData.totalItems}</strong> sản phẩm · Trang ${pageData.page}/${pageData.totalPages}</p>
</div>
</body>
</html>
