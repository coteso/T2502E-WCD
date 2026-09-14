<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>${formDTO.id == null ? 'Thêm' : 'Cập Nhật'} Sản Phẩm</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50 text-gray-800 p-6">
<div class="max-w-3xl mx-auto bg-white p-8 rounded-xl shadow-sm border">
    <h1 class="text-2xl font-bold mb-6">${formDTO.id == null ? 'Thêm Sản Phẩm Mới' : 'Cập Nhật Sản Phẩm'}</h1>

    <c:if test="${not empty errorMessage}">
        <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                ${errorMessage}
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/products/${formDTO.id == null ? 'create' : 'edit'}" class="space-y-6">
        <input type="hidden" name="id" value="${formDTO.id}">

        <!-- Product Block -->
        <div class="border-b pb-4">
            <h2 class="text-lg font-bold text-blue-600 mb-4">1. Thông Tin Cơ Bản (Product)</h2>
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label class="block text-sm font-semibold mb-1">Mã SKU *</label>
                    <input type="text" name="sku" value="${formDTO.sku}" required class="w-full border rounded-lg p-2 text-sm">
                </div>
                <div>
                    <label class="block text-sm font-semibold mb-1">Tên Sản Phẩm *</label>
                    <input type="text" name="name" value="${formDTO.name}" required class="w-full border rounded-lg p-2 text-sm">
                </div>
                <div>
                    <label class="block text-sm font-semibold mb-1">Đơn Giá (VND) *</label>
                    <input type="number" step="0.01" name="price" value="${formDTO.price}" required class="w-full border rounded-lg p-2 text-sm">
                </div>
                <div>
                    <label class="block text-sm font-semibold mb-1">Số Lượng Tồn Kho *</label>
                    <input type="number" name="quantity" value="${formDTO.quantity}" required class="w-full border rounded-lg p-2 text-sm">
                </div>
                <div>
                    <label class="block text-sm font-semibold mb-1">Danh Mục *</label>
                    <select name="categoryId" required class="w-full border rounded-lg p-2 text-sm">
                        <c:forEach items="${categories}" var="c">
                            <option value="${c.id}" ${formDTO.categoryId == c.id ? 'selected' : ''}>${c.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div>
                    <label class="block text-sm font-semibold mb-1">Trạng Thái</label>
                    <select name="status" class="w-full border rounded-lg p-2 text-sm">
                        <option value="true" ${formDTO.status != false ? 'selected' : ''}>Active</option>
                        <option value="false" ${formDTO.status == false ? 'selected' : ''}>Inactive</option>
                    </select>
                </div>
            </div>
        </div>

        <!-- ProductDetail Block -->
        <div class="pb-4">
            <h2 class="text-lg font-bold text-blue-600 mb-4">2. Chi Tiết Sản Phẩm (Product Detail - 1:1)</h2>
            <div class="grid grid-cols-3 gap-4 mb-4">
                <div>
                    <label class="block text-sm font-semibold mb-1">Nhà Sản Xuất</label>
                    <input type="text" name="manufacturer" value="${formDTO.manufacturer}" class="w-full border rounded-lg p-2 text-sm">
                </div>
                <div>
                    <label class="block text-sm font-semibold mb-1">Bảo Hành (Tháng)</label>
                    <input type="number" name="warrantyMonths" value="${formDTO.warrantyMonths}" class="w-full border rounded-lg p-2 text-sm">
                </div>
                <div>
                    <label class="block text-sm font-semibold mb-1">Xuất Xứ</label>
                    <input type="text" name="origin" value="${formDTO.origin}" class="w-full border rounded-lg p-2 text-sm">
                </div>
            </div>
            <div class="space-y-4">
                <div>
                    <label class="block text-sm font-semibold mb-1">Mô Tả Chi Tiết</label>
                    <textarea name="description" rows="2" class="w-full border rounded-lg p-2 text-sm">${formDTO.description}</textarea>
                </div>
                <div>
                    <label class="block text-sm font-semibold mb-1">Thông Số Kỹ Thuật</label>
                    <textarea name="technicalSpec" rows="2" class="w-full border rounded-lg p-2 text-sm">${formDTO.technicalSpec}</textarea>
                </div>
            </div>
        </div>

        <div class="flex justify-end gap-3">
            <a href="${pageContext.request.contextPath}/products" class="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg">Hủy Bỏ</a>
            <button type="submit" class="bg-blue-600 text-white px-6 py-2 rounded-lg font-semibold hover:bg-blue-700">Lưu Dữ Liệu</button>
        </div>
    </form>
</div>
</body>
</html>