<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xảy Ra Lỗi Nghiệp Vụ</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 flex items-center justify-center h-screen">
<div class="bg-white p-8 rounded-xl shadow-md border max-w-md text-center">
    <h1 class="text-3xl font-bold text-red-600 mb-2">Đã xảy ra lỗi!</h1>
    <p class="text-gray-600 mb-6">${errorMessage != null ? errorMessage : 'Yêu cầu không hợp lệ hoặc tài nguyên không tồn tại.'}</p>
    <a href="${pageContext.request.contextPath}/products" class="inline-block bg-slate-800 text-white px-4 py-2 rounded-lg hover:bg-slate-900">Trở về Trang Chủ</a>
</div>
</body>
</html>