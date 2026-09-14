<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản Lý Danh Mục</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50 text-gray-800 p-6">
<div class="max-w-5xl mx-auto">
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Quản Lý Danh Mục Category</h1>
        <a href="${pageContext.request.contextPath}/products" class="text-blue-600 hover:underline">&larr; Quay lại Sản Phẩm</a>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                ${errorMessage}
        </div>
    </c:if>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <!-- Form Container -->
        <div class="bg-white p-6 rounded-xl shadow-sm border h-fit">
            <h2 class="font-bold text-lg mb-4">Thêm / Sửa Danh Mục</h2>
            <form action="${pageContext.request.contextPath}/categories" method="post" class="space-y-4">
                <input type="hidden" name="id" id="catId">
                <div>
                    <label class="block text-sm font-semibold mb-1">Tên Danh Mục</label>
                    <input type="text" name="name" id="catName" required class="w-full border rounded-lg p-2 text-sm">
                </div>
                <div>
                    <label class="block text-sm font-semibold mb-1">Mô Tả</label>
                    <textarea name="description" id="catDesc" class="w-full border rounded-lg p-2 text-sm"></textarea>
                </div>
                <div>
                    <label class="block text-sm font-semibold mb-1">Trạng Thái</label>
                    <select name="status" id="catStatus" class="w-full border rounded-lg p-2 text-sm">
                        <option value="true">Active</option>
                        <option value="false">Inactive</option>
                    </select>
                </div>
                <button type="submit" class="w-full bg-blue-600 text-white p-2 rounded-lg font-semibold hover:bg-blue-700">Lưu Danh Mục</button>
            </form>
        </div>

        <!-- Table Container -->
        <div class="md:col-span-2 bg-white rounded-xl shadow-sm border overflow-hidden">
            <table class="w-full text-left border-collapse">
                <thead class="bg-slate-100 border-b text-xs uppercase">
                <tr>
                    <th class="p-3">ID</th>
                    <th class="p-3">Tên Danh Mục</th>
                    <th class="p-3">Mô Tả</th>
                    <th class="p-3">Trạng Thái</th>
                    <th class="p-3">Thao Tác</th>
                </tr>
                </thead>
                <tbody class="divide-y text-sm">
                <c:forEach items="${categories}" var="c">
                    <tr>
                        <td class="p-3 font-mono">${c.id}</td>
                        <td class="p-3 font-semibold">${c.name}</td>
                        <td class="p-3 text-gray-500">${c.description}</td>
                        <td class="p-3">
                                <span class="px-2 py-1 rounded-full text-xs font-bold ${c.status ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'}">
                                        ${c.status ? 'Active' : 'Inactive'}
                                </span>
                        </td>
                        <td class="p-3">
                            <button onclick="editCategory('${c.id}', '${c.name}', '${c.description}', ${c.status})" class="text-blue-600 hover:underline">Sửa</button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    function editCategory(id, name, desc, status) {
        document.getElementById('catId').value = id;
        document.getElementById('catName').value = name;
        document.getElementById('catDesc').value = desc;
        document.getElementById('catStatus').value = status;
    }
</script>
</body>
</html>