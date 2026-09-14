<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Danh Mục | ORM MVC System</title>
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

        .container {
            display: grid;
            grid-template-columns: 1fr 2fr;
            gap: 24px;
        }

        @media (max-width: 900px) {
            .container {
                grid-template-columns: 1fr;
            }
        }

        .card {
            background-color: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 14px;
            padding: 24px;
        }

        .card-title {
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 20px;
            color: var(--text-light);
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            font-size: 13px;
            font-weight: 500;
            color: var(--text-dim);
            margin-bottom: 6px;
        }

        input[type="text"], textarea {
            width: 100%;
            background-color: #0f172a;
            border: 1px solid var(--card-border);
            border-radius: 8px;
            padding: 10px 14px;
            color: var(--text-light);
            font-size: 14px;
            outline: none;
            transition: border-color 0.2s;
        }

        input[type="text"]:focus, textarea:focus {
            border-color: var(--primary);
        }

        .toggle-switch {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-top: 8px;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            padding: 10px 20px;
            border-radius: 8px;
            border: none;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s;
            gap: 6px;
        }

        .btn-primary {
            background-color: var(--primary);
            color: white;
            width: 100%;
        }

        .btn-primary:hover {
            background-color: var(--primary-hover);
        }

        .btn-secondary {
            background-color: #475569;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #334155;
        }

        .btn-sm {
            padding: 6px 12px;
            font-size: 12px;
        }

        .btn-warning {
            background-color: rgba(245, 158, 11, 0.2);
            color: #fbbf24;
            border: 1px solid rgba(245, 158, 11, 0.4);
        }

        .btn-warning:hover {
            background-color: rgba(245, 158, 11, 0.3);
        }

        .btn-danger {
            background-color: rgba(239, 68, 68, 0.2);
            color: #f87171;
            border: 1px solid rgba(239, 68, 68, 0.4);
        }

        .btn-danger:hover {
            background-color: rgba(239, 68, 68, 0.3);
        }

        .alert-error {
            background: rgba(239, 68, 68, 0.15);
            border: 1px solid rgba(239, 68, 68, 0.4);
            color: #fca5a5;
            padding: 14px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .table-responsive {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
        }

        th {
            background-color: #0f172a;
            color: var(--text-dim);
            text-align: left;
            padding: 12px 16px;
            font-weight: 600;
            border-bottom: 1px solid var(--card-border);
        }

        td {
            padding: 14px 16px;
            border-bottom: 1px solid var(--card-border);
            color: var(--text-light);
        }

        tr:hover {
            background-color: rgba(255, 255, 255, 0.02);
        }

        .badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 9999px;
            font-size: 12px;
            font-weight: 600;
        }

        .badge-active {
            background-color: rgba(16, 185, 129, 0.15);
            color: #34d399;
            border: 1px solid rgba(16, 185, 129, 0.3);
        }

        .badge-inactive {
            background-color: rgba(148, 163, 184, 0.15);
            color: #cbd5e1;
            border: 1px solid rgba(148, 163, 184, 0.3);
        }

        .actions {
            display: flex;
            gap: 8px;
        }
    </style>
</head>
<body>

    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/products" class="brand">ORM Product System</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/products" class="nav-link">Sản Phẩm</a>
            <a href="${pageContext.request.contextPath}/categories" class="nav-link active">Danh Mục</a>
            <c:if test="${not empty sessionScope.loggedUser}">
                <span style="color: var(--text-light); font-weight: 500; padding: 8px 12px; font-size: 13px;">
                    👤 <c:out value="${sessionScope.loggedUser.fullName}" default="${sessionScope.loggedUser.username}" />
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-danger" style="text-decoration:none;">Đăng Xuất</a>
            </c:if>
        </div>
    </nav>

    <c:if test="${not empty errorMessage}">
        <div class="alert-error">
            <strong>Lỗi:</strong> <c:out value="${errorMessage}" />
        </div>
    </c:if>

    <div class="container">
        <!-- Form Add/Edit Category -->
        <div class="card">
            <h2 class="card-title">
                <c:choose>
                    <c:when test="${not empty editCategory}">Cập Nhật Danh Mục</c:when>
                    <c:otherwise>➕ Thêm Danh Mục Mới</c:otherwise>
                </c:choose>
            </h2>

            <form action="${pageContext.request.contextPath}/categories" method="post">
                <c:choose>
                    <c:when test="${not empty editCategory}">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="${editCategory.id}">
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="action" value="save">
                    </c:otherwise>
                </c:choose>

                <div class="form-group">
                    <label for="name">Tên Danh Mục (*)</label>
                    <input type="text" id="name" name="name" required placeholder="Ví dụ: Điện Thoại, Laptop..." value="<c:out value='${editCategory.name}' />">
                </div>

                <div class="form-group">
                    <label for="description">Mô Tả</label>
                    <textarea id="description" name="description" rows="3" placeholder="Nhập mô tả ngắn..."><c:out value='${editCategory.description}' /></textarea>
                </div>

                <div class="form-group">
                    <label>Trạng Thái</label>
                    <div class="toggle-switch">
                        <input type="checkbox" id="status" name="status" <c:if test="${empty editCategory || editCategory.status}">checked</c:if>>
                        <label for="status" style="margin-bottom:0;">Hoạt động (Active)</label>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary">
                    <c:choose>
                        <c:when test="${not empty editCategory}">Lưu Thay Đổi</c:when>
                        <c:otherwise>Tạo Danh Mục</c:otherwise>
                    </c:choose>
                </button>

                <c:if test="${not empty editCategory}">
                    <a href="${pageContext.request.contextPath}/categories" class="btn btn-secondary" style="margin-top: 10px; width: 100%;">Hủy Chỉnh Sửa</a>
                </c:if>
            </form>
        </div>

        <!-- Category Table -->
        <div class="card">
            <h2 class="card-title">Danh Sách Danh Mục</h2>
            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tên Danh Mục</th>
                            <th>Mô Tả</th>
                            <th>Trạng Thái</th>
                            <th>Hành Động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cat" items="${categories}">
                            <tr>
                                <td>${cat.id}</td>
                                <td><strong><c:out value="${cat.name}" /></strong></td>
                                <td><c:out value="${cat.description}" default="--" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${cat.status}">
                                            <span class="badge badge-active">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-inactive">Inactive</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="actions">
                                        <a href="${pageContext.request.contextPath}/categories?editId=${cat.id}" class="btn btn-sm btn-warning">Sửa</a>
                                        
                                        <form action="${pageContext.request.contextPath}/categories" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="toggle">
                                            <input type="hidden" name="id" value="${cat.id}">
                                            <c:choose>
                                                <c:when test="${cat.status}">
                                                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc muốn vô hiệu hóa danh mục này?');">Disable</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="submit" class="btn btn-sm btn-secondary">Enable</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty categories}">
                            <tr>
                                <td colspan="5" style="text-align:center; color: var(--text-dim);">Chưa có danh mục nào.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>
