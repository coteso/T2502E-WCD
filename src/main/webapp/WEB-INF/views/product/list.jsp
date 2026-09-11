<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>${appPageTitle}</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .alert-success { color: green; background: #e6f4ea; padding: 10px; margin-bottom: 15px; border-radius: 4px; }
        .alert-error { color: red; background: #fce8e6; padding: 10px; margin-bottom: 15px; border-radius: 4px; }
        .filter-bar { margin-bottom: 20px; display: flex; gap: 10px; align-items: center; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #f2f2f2; }
        .btn { padding: 6px 12px; text-decoration: none; border-radius: 4px; display: inline-block; }
        .btn-add { background: #1a73e8; color: white; }
        .btn-edit { background: #fbbc05; color: black; margin-right: 5px; }
        .btn-delete { background: #ea4335; color: white; }
        .pagination { margin-top: 15px; display: flex; gap: 5px; }
        .page-link { padding: 5px 10px; border: 1px solid #ddd; text-decoration: none; color: #1a73e8; }
    </style>
</head>
<body>

    <h2>${appPageTitle}</h2>

    <!-- Hiển thị thông báo Flash Message từ Session hoặc Error từ Request -->
    <c:if test="${not empty sessionScope.message}">
        <div class="alert-success">${sessionScope.message}</div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert-error">${error}</div>
    </c:if>

    <!-- Thanh tìm kiếm và Bộ lọc Combobox theo danh mục (Yêu cầu giao diện mục 9) -->
    <div class="filter-bar">
        <form action="${pageContext.request.contextPath}/products" method="get" style="display:flex; gap:10px; margin:0;">
            <input type="hidden" name="action" value="list">

            <input type="text" name="searchName" placeholder="Tìm kiếm theo tên sản phẩm..." value="${param.searchName}" style="padding: 6px; width: 250px;">

            <select name="filterCategory" style="padding: 6px;">
                <option value="">-- Tất cả danh mục --</option>
                <c:forEach var="cat" items="${listCategories}">
                    <option value="${cat.id}" ${param.filterCategory == cat.id ? 'selected' : ''}>${cat.name}</option>
                </c:forEach>
            </select>

            <button type="submit" style="padding: 6px 15px; cursor: pointer;">Lọc / Tìm</button>
        </form>

        <a href="${pageContext.request.contextPath}/products?action=new" class="btn btn-add" style="margin-left: auto;">+ Thêm sản phẩm mới</a>
    </div>

    <!-- Bảng danh sách sản phẩm -->
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Tên sản phẩm</th>
                <th>Giá bán</th>
                <th>Số lượng</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${not empty listProducts}">
                    <c:forEach var="p" items="${listProducts}">
                        <tr>
                            <td>${p.id}</td>
                            <td><strong>${p.name}</strong></td>
                            <td>${p.price}</td>
                            <td>${p.quantity}</td>
                            <td>
                                <span style="color: ${p.status ? 'green' : 'red'}">
                                    ${p.status ? 'Đang bán' : 'Ngừng bán'}
                                </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/products?action=edit&id=${p.id}" class="btn btn-edit">Sửa</a>
                                <a href="${pageContext.request.contextPath}/products?action=delete&id=${p.id}" class="btn btn-delete" onclick="return confirm('Bạn chắc chắn muốn xóa sản phẩm này?');">Xóa</a>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="6" style="text-align: center; color: #888;">Không có sản phẩm nào phù hợp.</td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

    <!-- Phân trang giao diện mẫu (Yêu cầu mục 9) -->
    <div class="pagination">
        <a href="#" class="page-link">Previous</a>
        <a href="#" class="page-link" style="background:#1a73e8; color:white;">1</a>
        <a href="#" class="page-link">2</a>
        <a href="#" class="page-link">Next</a>
    </div>

</body>
</html>
