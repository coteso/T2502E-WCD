<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>${product != null ? 'Chỉnh sửa' : 'Thêm mới'} Sản phẩm</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-4 mb-5">
    <div class="card shadow-sm">
        <div class="card-header bg-dark text-white">
            <h4 class="mb-0">${product != null ? 'Chỉnh sửa' : 'Thêm mới'} Sản phẩm & Chi tiết (One-To-One)</h4>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}${product != null ? '/products/edit' : '/products/create'}" method="post">
                <input type="hidden" name="id" value="${product.id}">

                <h5 class="text-primary border-bottom pb-2">1. Thông tin cơ bản (Product)</h5>
                <div class="row g-3 mb-4">
                    <div class="col-md-4">
                        <label class="form-label">Mã SKU (* Unique)</label>
                        <input type="text" name="sku" value="${product.sku}" class="form-control" required>
                    </div>
                    <div class="col-md-8">
                        <label class="form-label">Tên sản phẩm (*)</label>
                        <input type="text" name="name" value="${product.name}" class="form-control" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Giá bán (* > 0)</label>
                        <input type="number" step="any" name="price" value="${product.price}" class="form-control" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Số lượng tồn (* >= 0)</label>
                        <input type="number" name="quantity" value="${product.quantity}" class="form-control" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Danh mục (*)</label>
                        <select name="categoryId" class="form-select" required>
                            <option value="">-- Chọn danh mục --</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.id}" <c:if test="${product != null && product.category.id == cat.id}">selected</c:if>>
                                    ${cat.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-12">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="status" id="prodStatus" 
                                   <c:if test="${product == null || product.status}">checked</c:if>>
                            <label class="form-check-label" for="prodStatus">Đang kinh doanh (Active)</label>
                        </div>
                    </div>
                </div>

                <h5 class="text-primary border-bottom pb-2">2. Thông tin chi tiết (ProductDetail)</h5>
                <div class="row g-3 mb-4">
                    <div class="col-md-4">
                        <label class="form-label">Nhà sản xuất</label>
                        <input type="text" name="manufacturer" value="${product.detail.manufacturer}" class="form-control">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Thời gian bảo hành (tháng)</label>
                        <input type="number" name="warrantyMonths" value="${product.detail.warrantyMonths}" class="form-control">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Xuất xứ</label>
                        <input type="text" name="origin" value="${product.detail.origin}" class="form-control">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Mô tả sản phẩm</label>
                        <textarea name="description" class="form-control" rows="3">${product.detail.description}</textarea>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">Thông số kỹ thuật</label>
                        <textarea name="technicalSpec" class="form-control" rows="3">${product.detail.technicalSpec}</textarea>
                    </div>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-success px-4">Lưu Dữ Liệu</button>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Hủy bỏ</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>