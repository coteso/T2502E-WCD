<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>${not empty product.id && product.id != 0 ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm mới'}</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .form-container { width: 450px; margin-top: 15px; }
        .form-group { margin-bottom: 15px; display: flex; flex-direction: column; gap: 5px; }
        label { font-weight: bold; }
        input[type="text"], input[type="number"], select { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
        .radio-group { display: flex; gap: 15px; padding: 5px 0; }
        .alert-error { color: red; background: #fce8e6; padding: 10px; margin-bottom: 15px; border-radius: 4px; }
        .btn-submit { background: #1a73e8; color: white; border: none; padding: 10px 15px; cursor: pointer; border-radius: 4px; font-weight: bold; }
        .btn-back { background: #f1f3f4; color: #3c4043; border: 1px solid #dadce0; padding: 9px 15px; text-decoration: none; border-radius: 4px; display: inline-block; text-align: center; }
    </style>
</head>
<body>

    <h2>${not empty product.id && product.id != 0 ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm mới'}</h2>

    <!-- Hiển thị thông báo lỗi validate dữ liệu nếu có -->
    <c:if test="${not empty error}">
        <div class="alert-error">${error}</div>
    </c:if>

    <div class="form-container">
        <!-- Chuyển hướng action xử lý: nếu có id thì gửi yêu cầu 'update', ngược lại gửi 'insert' -->
        <form action="${pageContext.request.contextPath}/products?action=${not empty product.id && product.id != 0 ? 'update' : 'insert'}" method="post">

            <!-- Giữ ID ẩn phục vụ cho quá trình Cập nhật sản phẩm -->
            <c:if test="${not empty product.id && product.id != 0}">
                <input type="hidden" name="id" value="${product.id}">
            </c:if>

            <div class="form-group">
                <label for="name">Tên sản phẩm:</label>
                <input type="text" id="name" name="name" value="${product.name}" required>
            </div>

            <div class="form-group">
                <label for="price">Giá bán (Double):</label>
                <input type="number" id="price" name="price" step="0.01" value="${product.price}" required>
            </div>

            <div class="form-group">
                <label for="quantity">Số lượng kho:</label>
                <input type="number" id="quantity" name="quantity" value="${product.quantity}" required>
            </div>

            <!-- Tải danh sách category trực tiếp từ database sang combobox (Yêu cầu mục 9) -->
            <div class="form-group">
                <label for="categoryId">Danh mục nhóm sản phẩm:</label>
                <select id="categoryId" name="categoryId" required>
                    <option value="">-- Chọn một danh mục --</option>
                    <c:forEach var="cat" items="${listCategories}">
                        <option value="${cat.id}" ${product.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                    </c:forEach>
                </select>
            </div>

            <!-- Trạng thái hoạt động (Chỉ xuất hiện/có ý nghĩa rõ khi cập nhật sản phẩm) -->
            <div class="form-group">
                <label>Trạng thái sản phẩm:</label>
                <div class="radio-group">
                    <label style="font-weight: normal;">
                        <input type="radio" name="status" value="true" ${product.id == 0 || product.status ? 'checked' : ''}> Đang kích hoạt
                    </label>
                    <label style="font-weight: normal;">
                        <input type="radio" name="status" value="false" ${not empty product.id && product.id != 0 && !product.status ? 'checked' : ''}> Tạm dừng kinh doanh
                    </label>
                </div>
            </div>

            <div style="display: flex; gap: 10px; margin-top: 20px;">
                <button type="submit" class="btn-submit">Lưu lại thông tin</button>
                <a href="${pageContext.request.contextPath}/products" class="btn-back">Quay về danh sách</a>
            </div>
        </form>
    </div>

</body>
</html>
