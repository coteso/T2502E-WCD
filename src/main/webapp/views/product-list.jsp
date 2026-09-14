<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh Sách Sản Phẩm | ORM MVC System</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg-dark: #0f172a;
            --card-bg: #1e293b;
            --card-border: #334155;
            --primary: #3b82f6;
            --primary-hover: #2563eb;
            --success: #10b981;
            --warning: #f59e0b;
            --danger: #ef4444;
            --text-light: #f8fafc;
            --text-dim: #94a3b8;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Inter', sans-serif;
        }

        body {
            background-color: var(--bg-dark);
            color: var(--text-light);
            min-height: 100vh;
            padding: 24px;
        }

        .navbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: rgba(30, 41, 59, 0.8);
            backdrop-filter: blur(12px);
            border: 1px solid var(--card-border);
            border-radius: 12px;
            padding: 16px 24px;
            margin-bottom: 24px;
        }

        .brand {
            font-size: 20px;
            font-weight: 700;
            color: #60a5fa;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .nav-links {
            display: flex;
            gap: 12px;
        }

        .nav-link {
            color: var(--text-dim);
            text-decoration: none;
            padding: 8px 16px;
            border-radius: 8px;
            font-weight: 500;
            transition: all 0.2s;
        }

        .nav-link:hover, .nav-link.active {
            color: var(--text-light);
            background: rgba(255, 255, 255, 0.1);
        }

        .search-card {
            background-color: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 14px;
            padding: 20px 24px;
            margin-bottom: 24px;
        }

        .search-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 14px;
            align-items: end;
        }

        label {
            display: block;
            font-size: 12px;
            font-weight: 600;
            color: var(--text-dim);
            margin-bottom: 6px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        input[type="text"], input[type="number"], select {
            width: 100%;
            background-color: #0f172a;
            border: 1px solid var(--card-border);
            border-radius: 8px;
            padding: 8px 12px;
            color: var(--text-light);
            font-size: 13px;
            outline: none;
        }

        input[type="text"]:focus, input[type="number"]:focus, select:focus {
            border-color: var(--primary);
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            padding: 9px 18px;
            border-radius: 8px;
            border: none;
            font-weight: 600;
            font-size: 13px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s;
            gap: 6px;
        }

        .btn-primary {
            background-color: var(--primary);
            color: white;
        }

        .btn-primary:hover {
            background-color: var(--primary-hover);
        }

        .btn-success {
            background-color: var(--success);
            color: white;
        }

        .btn-success:hover {
            background-color: #059669;
        }

        .btn-secondary {
            background-color: #475569;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #334155;
        }

        .btn-sm {
            padding: 5px 10px;
            font-size: 12px;
        }

        .btn-warning {
            background-color: rgba(245, 158, 11, 0.2);
            color: #fbbf24;
            border: 1px solid rgba(245, 158, 11, 0.4);
        }

        .btn-danger {
            background-color: rgba(239, 68, 68, 0.2);
            color: #f87171;
            border: 1px solid rgba(239, 68, 68, 0.4);
        }

        .header-action {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 16px;
        }

        .table-card {
            background-color: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 14px;
            padding: 20px;
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.3);
        }

        .table-responsive {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        th {
            background-color: #0f172a;
            color: var(--text-dim);
            text-align: left;
            padding: 12px 14px;
            font-weight: 600;
            border-bottom: 1px solid var(--card-border);
            white-space: nowrap;
        }

        td {
            padding: 14px;
            border-bottom: 1px solid var(--card-border);
            color: var(--text-light);
            vertical-align: middle;
        }

        tr:hover {
            background-color: rgba(255, 255, 255, 0.02);
        }

        .badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 6px;
            font-size: 11px;
            font-weight: 600;
            letter-spacing: 0.3px;
        }

        .badge-sku {
            background: rgba(96, 165, 250, 0.15);
            color: #93c5fd;
            border: 1px solid rgba(96, 165, 250, 0.3);
        }

        .badge-category {
            background: rgba(168, 85, 247, 0.15);
            color: #c084fc;
            border: 1px solid rgba(168, 85, 247, 0.3);
        }

        .badge-active {
            background: rgba(16, 185, 129, 0.15);
            color: #34d399;
            border: 1px solid rgba(16, 185, 129, 0.3);
        }

        .badge-inactive {
            background: rgba(148, 163, 184, 0.15);
            color: #cbd5e1;
            border: 1px solid rgba(148, 163, 184, 0.3);
        }

        .price-text {
            color: #34d399;
            font-weight: 700;
        }

        .detail-preview {
            font-size: 11px;
            color: var(--text-dim);
            line-height: 1.4;
        }

        .pagination {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 20px;
            padding-top: 16px;
            border-top: 1px solid var(--card-border);
        }

        .pagination-links {
            display: flex;
            gap: 6px;
        }

        .page-link {
            padding: 6px 12px;
            border-radius: 6px;
            background-color: #0f172a;
            border: 1px solid var(--card-border);
            color: var(--text-dim);
            text-decoration: none;
            font-size: 12px;
            font-weight: 500;
        }

        .page-link.active, .page-link:hover {
            background-color: var(--primary);
            color: white;
            border-color: var(--primary);
        }

        .alert-success {
            background: rgba(16, 185, 129, 0.15);
            border: 1px solid rgba(16, 185, 129, 0.4);
            color: #6ee7b7;
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 13px;
        }
    </style>
</head>
<body>

    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/products" class="brand">ORM Product System</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/products" class="nav-link active">Sản Phẩm</a>
            <a href="${pageContext.request.contextPath}/categories" class="nav-link">Danh Mục</a>
            <c:if test="${not empty sessionScope.loggedUser}">
                <span style="color: var(--text-light); font-weight: 500; padding: 8px 12px; font-size: 13px;">
                    👤 <c:out value="${sessionScope.loggedUser.fullName}" default="${sessionScope.loggedUser.username}" />
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-danger" style="text-decoration:none;">Đăng Xuất</a>
            </c:if>
        </div>
    </nav>

    <c:if test="${param.msg eq 'created'}">
        <div class="alert-success">✅ Thêm sản phẩm và chi tiết thành công trong 1 Transaction!</div>
    </c:if>
    <c:if test="${param.msg eq 'updated'}">
        <div class="alert-success">✅ Cập nhật sản phẩm và chi tiết thành công!</div>
    </c:if>
    <c:if test="${param.msg eq 'deleted'}">
        <div class="alert-success">🗑️ Đã xóa mềm sản phẩm (soft deleted = true)!</div>
    </c:if>

    <!-- Advanced Filter & Search Card -->
    <div class="search-card">
        <form action="${pageContext.request.contextPath}/products" method="get">
            <div class="search-grid">
                <div>
                    <label for="keyword">Từ khóa (Tên / SKU)</label>
                    <input type="text" id="keyword" name="keyword" placeholder="Nhập tên hoặc SKU..." value="<c:out value='${searchCriteria.keyword}' />">
                </div>

                <div>
                    <label for="categoryId">Danh mục</label>
                    <select id="categoryId" name="categoryId">
                        <option value="">-- Tất cả danh mục --</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.id}" <c:if test="${searchCriteria.categoryId eq cat.id}">selected</c:if>>
                                <c:out value="${cat.name}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div>
                    <label for="status">Trạng thái</label>
                    <select id="status" name="status">
                        <option value="">-- Tất cả --</option>
                        <option value="active" <c:if test="${searchCriteria.status eq true}">selected</c:if>>Active</option>
                        <option value="inactive" <c:if test="${searchCriteria.status eq false}">selected</c:if>>Inactive</option>
                    </select>
                </div>

                <div>
                    <label for="minPrice">Giá từ</label>
                    <input type="number" id="minPrice" name="minPrice" placeholder="0" value="${searchCriteria.minPrice}">
                </div>

                <div>
                    <label for="maxPrice">Giá đến</label>
                    <input type="number" id="maxPrice" name="maxPrice" placeholder="Max" value="${searchCriteria.maxPrice}">
                </div>

                <div>
                    <label for="sortBy">Sắp xếp theo</label>
                    <select id="sortBy" name="sortBy">
                        <option value="id" <c:if test="${searchCriteria.sortBy eq 'id'}">selected</c:if>>Mới nhất (ID)</option>
                        <option value="name" <c:if test="${searchCriteria.sortBy eq 'name'}">selected</c:if>>Tên sản phẩm</option>
                        <option value="price" <c:if test="${searchCriteria.sortBy eq 'price'}">selected</c:if>>Giá sản phẩm</option>
                        <option value="sku" <c:if test="${searchCriteria.sortBy eq 'sku'}">selected</c:if>>Mã SKU</option>
                    </select>
                </div>

                <div>
                    <label for="sortDir">Thứ tự</label>
                    <select id="sortDir" name="sortDir">
                        <option value="desc" <c:if test="${searchCriteria.sortDir eq 'desc'}">selected</c:if>>Giảm dần (DESC)</option>
                        <option value="asc" <c:if test="${searchCriteria.sortDir eq 'asc'}">selected</c:if>>Tăng dần (ASC)</option>
                    </select>
                </div>

                <div style="display:flex; gap: 8px;">
                    <button type="submit" class="btn btn-primary" style="flex:1;">Lọc</button>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Đặt lại</a>
                </div>
            </div>
        </form>
    </div>

    <!-- Product List Table Card -->
    <div class="table-card">
        <div class="header-action">
            <h2>🛒 Danh Sách Sản Phẩm (<c:out value="${totalItems}" />)</h2>
            <a href="${pageContext.request.contextPath}/products/create" class="btn btn-success">➕ Thêm Sản Phẩm Mới</a>
        </div>

        <div class="table-responsive">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>SKU</th>
                        <th>Tên Sản Phẩm</th>
                        <th>Danh Mục</th>
                        <th>Giá Bán</th>
                        <th>Tồn Kho</th>
                        <th>Chi Tiết (1:1 Detail)</th>
                        <th>Trạng Thái</th>
                        <th>Hành Động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="prod" items="${products}">
                        <tr>
                            <td>${prod.id}</td>
                            <td><span class="badge badge-sku"><c:out value="${prod.sku}" /></span></td>
                            <td><strong><c:out value="${prod.name}" /></strong></td>
                            <td><span class="badge badge-category"><c:out value="${prod.category.name}" /></span></td>
                            <td class="price-text">
                                <fmt:formatNumber value="${prod.price}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                            </td>
                            <td>${prod.quantity}</td>
                            <td>
                                <div class="detail-preview">
                                    <c:choose>
                                        <c:when test="${not empty prod.detail}">
                                            <span> Hang: <c:out value="${prod.detail.manufacturer}" default="N/A" /></span><br>
                                            <span>️ Bảo hành: ${prod.detail.warrantyMonths} tháng</span><br>
                                            <span> Specs: <c:out value="${prod.detail.technicalSpec}" default="N/A" /></span>
                                        </c:when>
                                        <c:otherwise>
                                            <em>(Chưa có chi tiết)</em>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${prod.status}">
                                        <span class="badge badge-active">Active</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-inactive">Inactive</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div style="display:flex; gap:6px;">
                                    <a href="${pageContext.request.contextPath}/products/edit?id=${prod.id}" class="btn btn-sm btn-warning">Sửa</a>
                                    
                                    <form action="${pageContext.request.contextPath}/products/delete" method="post" style="display:inline;">
                                        <input type="hidden" name="id" value="${prod.id}">
                                        <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc chắn muốn XÓA MỀM sản phẩm này không?');">Xóa Mềm</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty products}">
                        <tr>
                            <td colspan="9" style="text-align:center; padding: 30px; color: var(--text-dim);">
                                Không tìm thấy sản phẩm nào phù hợp với bộ lọc.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <div style="font-size:12px; color: var(--text-dim);">
                    Trang ${searchCriteria.page} / ${totalPages} (Tổng số: ${totalItems} kết quả)
                </div>
                <div class="pagination-links">
                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <a href="${pageContext.request.contextPath}/products?page=${i}&keyword=${searchCriteria.keyword}&categoryId=${searchCriteria.categoryId}&status=${param.status}&minPrice=${searchCriteria.minPrice}&maxPrice=${searchCriteria.maxPrice}&sortBy=${searchCriteria.sortBy}&sortDir=${searchCriteria.sortDir}"
                           class="page-link <c:if test='${i eq searchCriteria.page}'>active</c:if>">
                            ${i}
                        </a>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </div>
</body>
</html>
