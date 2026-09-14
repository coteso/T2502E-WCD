<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:choose><c:when test="${isEdit}">Sửa Sản Phẩm</c:when><c:otherwise>Thêm Sản Phẩm Mới</c:otherwise></c:choose> | ORM MVC System</title>
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
        }

        .nav-link:hover, .nav-link.active {
            color: var(--text-light);
            background: rgba(255, 255, 255, 0.1);
        }

        .form-container {
            max-width: 900px;
            margin: 0 auto;
            background-color: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 32px;
            box-shadow: 0 20px 25px -5px rgba(0,0,0,0.4);
        }

        .form-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
            padding-bottom: 16px;
            border-bottom: 1px solid var(--card-border);
        }

        .form-title {
            font-size: 22px;
            font-weight: 700;
            color: var(--text-light);
        }

        .section-title {
            font-size: 16px;
            font-weight: 600;
            color: #60a5fa;
            margin: 24px 0 16px 0;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .grid-2 {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        @media (max-width: 768px) {
            .grid-2 {
                grid-template-columns: 1fr;
            }
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            font-size: 13px;
            font-weight: 500;
            color: var(--text-dim);
            margin-bottom: 6px;
        }

        input[type="text"], input[type="number"], select, textarea {
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

        input[type="text"]:focus, input[type="number"]:focus, select:focus, textarea:focus {
            border-color: var(--primary);
        }

        .checkbox-group {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-top: 10px;
        }

        .alert-error {
            background: rgba(239, 68, 68, 0.15);
            border: 1px solid rgba(239, 68, 68, 0.4);
            color: #fca5a5;
            padding: 14px;
            border-radius: 8px;
            margin-bottom: 24px;
            font-size: 14px;
        }

        .form-actions {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            margin-top: 32px;
            padding-top: 20px;
            border-top: 1px solid var(--card-border);
        }

        .btn {
            padding: 12px 24px;
            border-radius: 8px;
            border: none;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s;
        }

        .btn-primary {
            background-color: var(--primary);
            color: white;
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

    <div class="form-container">
        <div class="form-header">
            <h1 class="form-title">
                <c:choose>
                    <c:when test="${isEdit}">Chỉnh Sửa Sản Phẩm & Chi Tiết</c:when>
                    <c:otherwise>➕ Thêm Mới Sản Phẩm & Chi Tiết</c:otherwise>
                </c:choose>
            </h1>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Quay Lại</a>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert-error">
                <strong>Lỗi nghiệp vụ / Transaction Rollback:</strong> <c:out value="${errorMessage}" />
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/products/${isEdit ? 'edit' : 'create'}" method="post">
            <c:if test="${isEdit}">
                <input type="hidden" name="id" value="${formDTO.id}">
            </c:if>

            <!-- Product Information -->
            <div class="section-title">THÔNG TIN SẢN PHẨM CHÍNH (PRODUCT)</div>
            <div class="grid-2">
                <div class="form-group">
                    <label for="name">Tên Sản Phẩm (*)</label>
                    <input type="text" id="name" name="name" required placeholder="Nhập tên sản phẩm..." value="<c:out value='${formDTO.name}' />">
                </div>

                <div class="form-group">
                    <label for="sku">Mã SKU (* Unique)</label>
                    <input type="text" id="sku" name="sku" required placeholder="Ví dụ: SKU-IPHONE-15" value="<c:out value='${formDTO.sku}' />">
                </div>

                <div class="form-group">
                    <label for="price">Giá Bán (VNĐ) (* > 0)</label>
                    <input type="number" id="price" name="price" step="0.01" min="0.01" required placeholder="0.00" value="${formDTO.price}">
                </div>

                <div class="form-group">
                    <label for="quantity">Tồn Kho (* >= 0)</label>
                    <input type="number" id="quantity" name="quantity" min="0" required placeholder="0" value="${formDTO.quantity != null ? formDTO.quantity : 0}">
                </div>

                <div class="form-group">
                    <label for="categoryId">Danh Mục (*)</label>
                    <select id="categoryId" name="categoryId" required>
                        <option value="">-- Chọn danh mục --</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.id}" <c:if test="${formDTO.categoryId eq cat.id}">selected</c:if>>
                                <c:out value="${cat.name}" /> <c:if test="${not cat.status}">(Đã khóa)</c:if>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label>Trạng Thái Sản Phẩm</label>
                    <div class="checkbox-group">
                        <input type="checkbox" id="status" name="status" <c:if test="${empty formDTO.status || formDTO.status}">checked</c:if>>
                        <label for="status" style="margin-bottom:0;">Đang Kinh Doanh (Active)</label>
                    </div>
                </div>
            </div>

            <!-- Product Detail Information -->
            <div class="section-title">CHI TIẾT SẢN PHẨM (PRODUCT DETAIL - 1:1)</div>
            <div class="grid-2">
                <div class="form-group">
                    <label for="manufacturer">Nhà Sản Xuất (Manufacturer)</label>
                    <input type="text" id="manufacturer" name="manufacturer" placeholder="Ví dụ: Apple, Samsung, Sony..." value="<c:out value='${formDTO.manufacturer}' />">
                </div>

                <div class="form-group">
                    <label for="warrantyMonths">Bảo Hành (Tháng) (* >= 0)</label>
                    <input type="number" id="warrantyMonths" name="warrantyMonths" min="0" placeholder="12" value="${formDTO.warrantyMonths != null ? formDTO.warrantyMonths : 12}">
                </div>

                <div class="form-group">
                    <label for="origin">Xuất Xứ (Origin)</label>
                    <input type="text" id="origin" name="origin" placeholder="Ví dụ: Việt Nam, Nhật Bản, Mỹ..." value="<c:out value='${formDTO.origin}' />">
                </div>
            </div>

            <div class="form-group">
                <label for="detailDescription">Mô Tả Chi Tiết Sản Phẩm</label>
                <textarea id="detailDescription" name="detailDescription" rows="3" placeholder="Nhập mô tả tính năng nổi bật..."><c:out value='${formDTO.detailDescription}' /></textarea>
            </div>

            <div class="form-group">
                <label for="technicalSpec">Thông Số Kỹ Thuật (Technical Spec)</label>
                <textarea id="technicalSpec" name="technicalSpec" rows="4" placeholder="RAM: 8GB, Chip: M2, Màn hình: 6.1 inch..."><c:out value='${formDTO.technicalSpec}' /></textarea>
            </div>

            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Hủy Bỏ</a>
                <button type="submit" class="btn btn-primary">
                    <c:choose>
                        <c:when test="${isEdit}">Lưu Cập Nhật (Transaction Sync)</c:when>
                        <c:otherwise>Lưu Sản Phẩm + Chi Tiết (Transaction)</c:otherwise>
                    </c:choose>
                </button>
            </div>
        </form>
    </div>
</body>
</html>
