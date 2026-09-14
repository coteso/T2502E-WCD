<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Lỗi Hệ Thống</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-5 text-center">
    <h1 class="text-danger">Đã Xảy Ra Lỗi!</h1>
    <p class="lead">${error != null ? error : 'Yêu cầu không hợp lệ.'}</p>
    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-3">Quay Lại Trang Chủ</a>
</body>
</html>