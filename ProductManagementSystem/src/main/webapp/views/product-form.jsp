<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>${product.id != null ? 'Sửa' : 'Thêm'} Sản Phẩm</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-4 mb-5">
    <h2>${product.id != null ? 'Chỉnh Sửa Sản Phẩm' : 'Thêm Sản Phẩm Mới'}</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/products/${product.id != null ? 'edit' : 'create'}" method="post">
        <input type="hidden" name="id" value="${product.id}">

        <div class="row">
            <div class="col-md-6">
                <h4>Thông Tin Cơ Bản</h4>
                <div class="mb-3">
                    <label class="form-label">SKU</label>
                    <input type="text" name="sku" class="form-control" value="${product.sku}" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Tên Sản Phẩm</label>
                    <input type="text" name="name" class="form-control" value="${product.name}" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Danh Mục</label>
                    <select name="categoryId" class="form-select" required>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.id}" ${product.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="mb-3">
                    <label class="form-label">Giá (VND)</label>
                    <input type="number" name="price" class="form-control" value="${product.price}" required min="1">
                </div>
                <div class="mb-3">
                    <label class="form-label">Số Lượng</label>
                    <input type="number" name="quantity" class="form-control" value="${product.quantity}" required min="0">
                </div>
                <div class="mb-3">
                    <label class="form-label">Trạng Thái</label>
                    <select name="status" class="form-select">
                        <option value="true" ${product.status != false ? 'selected' : ''}>Kích Hoạt</option>
                        <option value="false" ${product.status == false ? 'selected' : ''}>Vô Hiệu</option>
                    </select>
                </div>
            </div>

            <div class="col-md-6">
                <h4>Chi Tiết Kỹ Thuật</h4>
                <div class="mb-3">
                    <label class="form-label">Hãng Sản Xuất</label>
                    <input type="text" name="manufacturer" class="form-control" value="${product.manufacturer}">
                </div>
                <div class="mb-3">
                    <label class="form-label">Xuất Xứ</label>
                    <input type="text" name="origin" class="form-control" value="${product.origin}">
                </div>
                <div class="mb-3">
                    <label class="form-label">Bảo Hành (Tháng)</label>
                    <input type="number" name="warrantyMonths" class="form-control" value="${product.warrantyMonths}" required min="0">
                </div>
                <div class="mb-3">
                    <label class="form-label">Mô Tả</label>
                    <textarea name="description" class="form-control" rows="2">${product.description}</textarea>
                </div>
                <div class="mb-3">
                    <label class="form-label">Thông Số Kỹ Thuật</label>
                    <textarea name="technicalSpec" class="form-control" rows="2">${product.technicalSpec}</textarea>
                </div>
            </div>
        </div>

        <button type="submit" class="btn btn-primary mt-3">Lưu Dữ Liệu</button>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary mt-3">Quay Lại</a>
    </form>
</body>
</html>