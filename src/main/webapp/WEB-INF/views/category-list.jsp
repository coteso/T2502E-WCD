<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý danh mục | Product ORM MVC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">

    <header class="page-header">
        <h1>Quản lý danh mục</h1>
        <nav>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/products">Sản phẩm</a>
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

    <%-- Form thêm/sửa category (đổ dữ liệu khi bấm "Sửa") --%>
    <form method="post" action="${pageContext.request.contextPath}/categories" class="card">
        <c:choose>
            <c:when test="${not empty editCategory}">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="${editCategory.id}">
            </c:when>
            <c:otherwise>
                <input type="hidden" name="action" value="save">
            </c:otherwise>
        </c:choose>

        <div class="form-row">
            <div class="form-group">
                <label for="name">Tên danh mục <span class="required">*</span></label>
                <input type="text" id="name" name="name"
                       value="${editCategory.name}" placeholder="VD: Điện thoại">
            </div>
            <div class="form-group">
                <label for="description">Mô tả</label>
                <input type="text" id="description" name="description"
                       value="${editCategory.description}" placeholder="Mô tả ngắn">
            </div>
            <div class="form-group form-actions-inline">
                <button type="submit" class="btn btn-primary">
                    ${not empty editCategory ? 'Cập nhật' : 'Thêm danh mục'}
                </button>
                <c:if test="${not empty editCategory}">
                    <a class="btn btn-outline" href="${pageContext.request.contextPath}/categories">Huỷ</a>
                </c:if>
            </div>
        </div>
    </form>

    <table class="table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Tên danh mục</th>
            <th>Mô tả</th>
            <th>Trạng thái</th>
            <th>SP đang bán</th>
            <th>Tổng SP</th>
            <th>Hành động</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${not empty listCategories}">
                <c:forEach var="row" items="${listCategories}">
                    <tr>
                        <td>${row.id}</td>
                        <td><strong>${row.name}</strong></td>
                        <td>${row.description}</td>
                        <td>
                            <span class="badge ${row.status ? 'badge-active' : 'badge-inactive'}">
                                ${row.status ? 'Hoạt động' : 'Đã vô hiệu'}
                            </span>
                        </td>
                        <td class="text-right">${row.activeProducts}</td>
                        <td class="text-right">${row.totalProducts}</td>
                        <td>
                            <%-- Bật/tắt trạng thái - Service chặn disable khi còn product active --%>
                            <form method="post" style="display:inline"
                                  action="${pageContext.request.contextPath}/categories">
                                <input type="hidden" name="action" value="status">
                                <input type="hidden" name="id" value="${row.id}">
                                <input type="hidden" name="status" value="${row.status ? 'false' : 'true'}">
                                <button type="submit"
                                        class="btn btn-sm ${row.status ? 'btn-warning' : 'btn-success'}"
                                        ${row.status and row.activeProducts > 0 ? 'disabled title="Còn sản phẩm đang bán - không thể vô hiệu hoá"' : ''}>
                                    ${row.status ? 'Vô hiệu hoá' : 'Kích hoạt'}
                                </button>
                            </form>

                            <a class="btn btn-sm btn-outline"
                               href="${pageContext.request.contextPath}/categories?editId=${row.id}">Sửa</a>

                            <%-- Delete chỉ thành công khi category không còn product nào --%>
                            <form method="post" style="display:inline"
                                  action="${pageContext.request.contextPath}/categories"
                                  onsubmit="return confirm('Xoá danh mục này? (chỉ thành công khi không còn sản phẩm)');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${row.id}">
                                <button type="submit" class="btn btn-sm btn-danger">Xoá</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr><td colspan="7" class="empty-row">Chưa có danh mục nào.</td></tr>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>

    <p class="muted">
        Lưu ý: danh mục chỉ xoá được khi không còn sản phẩm tham chiếu;
        chỉ vô hiệu hoá được khi không còn sản phẩm <em>đang bán</em>.
    </p>
</div>
</body>
</html>
