<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>${empty form.id ? 'Thêm' : 'Sửa'} sản phẩm | Product ORM MVC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container container-narrow">

    <header class="page-header">
        <h1>${empty form.id ? 'Thêm sản phẩm mới' : 'Cập nhật sản phẩm'}</h1>
        <nav>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/products">&laquo; Về danh sách</a>
        </nav>
    </header>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <%-- Product + ProductDetail nhập trên CÙNG màn hình (mục 11 của đề) --%>
    <form method="post"
          action="${pageContext.request.contextPath}/products/${empty form.id ? 'create' : 'edit'}"
          class="card">

        <c:if test="${not empty form.id}">
            <input type="hidden" name="id" value="${form.id}">
        </c:if>
        <c:if test="${not empty form.detailId}">
            <input type="hidden" name="detailId" value="${form.detailId}">
        </c:if>

        <fieldset>
            <legend>Thông tin sản phẩm</legend>

            <div class="form-group">
                <label for="sku">SKU <span class="muted">(để trống sẽ tự sinh)</span></label>
                <input type="text" id="sku" name="sku" value="${form.sku}">
                <span class="field-error">${errors.sku}</span>
            </div>

            <div class="form-group">
                <label for="name">Tên sản phẩm <span class="required">*</span></label>
                <input type="text" id="name" name="name" value="${form.name}">
                <span class="field-error">${errors.name}</span>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="price">Giá <span class="required">*</span></label>
                    <input type="text" id="price" name="price" value="${form.price}">
                    <span class="field-error">${errors.price}</span>
                </div>
                <div class="form-group">
                    <label for="quantity">Tồn kho <span class="required">*</span></label>
                    <input type="text" id="quantity" name="quantity" value="${form.quantity}">
                    <span class="field-error">${errors.quantity}</span>
                </div>
            </div>

            <div class="form-group">
                <label for="categoryId">Danh mục <span class="required">*</span></label>
                <select id="categoryId" name="categoryId">
                    <option value="">-- Chọn danh mục --</option>
                    <c:forEach var="cat" items="${listCategories}">
                        <option value="${cat.id}" ${form.categoryId == cat.id ? 'selected' : ''}>
                                ${cat.name}
                        </option>
                    </c:forEach>
                </select>
                <span class="field-error">${errors.categoryId}</span>
            </div>

            <div class="form-group form-check">
                <label>
                    <input type="checkbox" name="status" value="true"
                           ${(empty form.status or form.status == 'true') ? 'checked' : ''}>
                    Đang bán (active)
                </label>
            </div>
        </fieldset>

        <fieldset>
            <legend>Chi tiết sản phẩm (ProductDetail)</legend>

            <div class="form-row">
                <div class="form-group">
                    <label for="manufacturer">Nhà sản xuất</label>
                    <input type="text" id="manufacturer" name="manufacturer" value="${form.manufacturer}">
                </div>
                <div class="form-group">
                    <label for="warrantyMonths">Bảo hành (tháng)</label>
                    <input type="text" id="warrantyMonths" name="warrantyMonths" value="${form.warrantyMonths}">
                    <span class="field-error">${errors.warrantyMonths}</span>
                </div>
            </div>

            <div class="form-group">
                <label for="origin">Xuất xứ</label>
                <input type="text" id="origin" name="origin" value="${form.origin}">
            </div>

            <div class="form-group">
                <label for="detailDescription">Mô tả</label>
                <textarea id="detailDescription" name="detailDescription" rows="3">${form.detailDescription}</textarea>
            </div>

            <div class="form-group">
                <label for="technicalSpec">Thông số kỹ thuật</label>
                <textarea id="technicalSpec" name="technicalSpec" rows="3">${form.technicalSpec}</textarea>
            </div>
        </fieldset>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">${empty form.id ? 'Tạo mới' : 'Lưu thay đổi'}</button>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/products">Huỷ</a>
        </div>
    </form>
</div>
</body>
</html>
