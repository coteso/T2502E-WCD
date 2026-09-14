<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản Lý Sản Phẩm</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50 text-gray-800">
<div class="max-w-7xl mx-auto p-6">
    <!-- Navbar -->
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold text-slate-800">Danh Sách Sản Phẩm</h1>
        <div>
            <a href="${pageContext.request.contextPath}/categories" class="bg-slate-600 text-white px-4 py-2 rounded-lg hover:bg-slate-700 mr-2">Quản Lý Danh Mục</a>
            <a href="${pageContext.request.contextPath}/products/create" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">+ Thêm Sản Phẩm</a>
        </div>
    </div>

    <!-- Filter Form -->
    <form method="get" action="${pageContext.request.contextPath}/products" class="bg-white p-4 rounded-xl shadow-sm border mb-6 grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
            <label class="block text-xs font-semibold uppercase text-gray-500 mb-1">Từ khóa</label>
            <input type="text" name="keyword" value="${criteria.keyword}" placeholder="Tên hoặc SKU..." class="w-full border rounded-lg p-2 text-sm">
        </div>
        <div>
            <label class="block text-xs font-semibold uppercase text-gray-500 mb-1">Danh mục</label>
            <select name="categoryId" class="w-full border rounded-lg p-2 text-sm">
                <option value="">-- Tất cả --</option>
                <c:forEach items="${categories}" var="c">
                    <option value="${c.id}" ${criteria.categoryId == c.id ? 'selected' : ''}>${c.name}</option>
                </c:forEach>
            </select>
        </div>
        <div>
            <label class="block text-xs font-semibold uppercase text-gray-500 mb-1">Trạng thái</label>
            <select name="status" class="w-full border rounded-lg p-2 text-sm">
                <option value="">-- Tất cả --</option>
                <option value="true" ${criteria.status == true ? 'selected' : ''}>Active</option>
                <option value="false" ${criteria.status == false ? 'selected' : ''}>Inactive</option>
            </select>
        </div>
        <div>
            <label class="block text-xs font-semibold uppercase text-gray-500 mb-1">Giá từ - Đến</label>
            <div class="flex gap-2">
                <input type="number" name="minPrice" value="${criteria.minPrice}" placeholder="Min" class="w-1/2 border rounded-lg p-2 text-sm">
                <input type="number" name="maxPrice" value="${criteria.maxPrice}" placeholder="Max" class="w-1/2 border rounded-lg p-2 text-sm">
            </div>
        </div>
        <div>
            <label class="block text-xs font-semibold uppercase text-gray-500 mb-1">Sắp xếp theo</label>
            <select name="sortBy" class="w-full border rounded-lg p-2 text-sm">
                <option value="createdAt" ${criteria.sortBy == 'createdAt' ? 'selected' : ''}>Ngày tạo</option>
                <option value="price" ${criteria.sortBy == 'price' ? 'selected' : ''}>Giá sản phẩm</option>
                <option value="name" ${criteria.sortBy == 'name' ? 'selected' : ''}>Tên sản phẩm</option>
            </select>
        </div>
        <div>
            <label class="block text-xs font-semibold uppercase text-gray-500 mb-1">Thứ tự</label>
            <select name="sortDir" class="w-full border rounded-lg p-2 text-sm">
                <option value="desc" ${criteria.sortDir == 'desc' ? 'selected' : ''}>Giảm dần</option>
                <option value="asc" ${criteria.sortDir == 'asc' ? 'selected' : ''}>Tăng dần</option>
            </select>
        </div>
        <div class="md:col-span-2 flex items-end">
            <button type="submit" class="w-full bg-slate-800 text-white p-2 rounded-lg font-semibold hover:bg-slate-900">Áp Dụng Bộ Lọc</button>
        </div>
    </form>

    <!-- Product Table -->
    <div class="bg-white rounded-xl shadow-sm border overflow-hidden">
        <table class="w-full text-left border-collapse">
            <thead class="bg-slate-100 border-b text-xs uppercase text-slate-600">
            <tr>
                <th class="p-3">SKU</th>
                <th class="p-3">Sản Phẩm</th>
                <th class="p-3">Danh Mục</th>
                <th class="p-3">Giá (VND)</th>
                <th class="p-3">Tồn Kho</th>
                <th class="p-3">Trạng Thái</th>
                <th class="p-3 text-right">Thao Tác</th>
            </tr>
            </thead>
            <tbody class="divide-y text-sm">
            <c:forEach items="${products}" var="p">
                <tr class="hover:bg-slate-50">
                    <td class="p-3 font-mono font-semibold">${p.sku}</td>
                    <td class="p-3 font-medium">${p.name}</td>
                    <td class="p-3"><span class="bg-slate-200 px-2 py-1 rounded text-xs font-semibold">${p.category.name}</span></td>
                    <td class="p-3 font-semibold text-emerald-600">${p.price}</td>
                    <td class="p-3">${p.quantity}</td>
                    <td class="p-3">
                            <span class="px-2 py-1 rounded-full text-xs font-bold ${p.status ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'}">
                                    ${p.status ? 'Active' : 'Inactive'}
                            </span>
                    </td>
                    <td class="p-3 text-right space-x-2">
                        <a href="${pageContext.request.contextPath}/products/edit?id=${p.id}" class="text-blue-600 hover:underline">Sửa</a>
                        <form action="${pageContext.request.contextPath}/products/delete?id=${p.id}" method="post" class="inline" onsubmit="return confirm('Xác nhận xóa mềm sản phẩm này?')">
                            <button type="submit" class="text-red-600 hover:underline">Xóa</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- Pagination -->
    <div class="flex justify-center mt-6 gap-2">
        <c:forEach begin="1" end="${totalPages}" var="i">
            <a href="${pageContext.request.contextPath}/products?page=${i}&keyword=${criteria.keyword}&categoryId=${criteria.categoryId}&status=${criteria.status}&minPrice=${criteria.minPrice}&maxPrice=${criteria.maxPrice}&sortBy=${criteria.sortBy}&sortDir=${criteria.sortDir}"
               class="px-3 py-1 rounded border ${criteria.page == i ? 'bg-blue-600 text-white' : 'bg-white text-slate-700'}">
                    ${i}
            </a>
        </c:forEach>
    </div>
</div>
</body>
</html>