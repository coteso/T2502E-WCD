<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thông báo lỗi</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center vh-100">
    <div class="container text-center">
        <div class="card shadow-sm mx-auto" style="max-width: 500px;">
            <div class="card-body p-4">
                <h3 class="text-danger mb-3">Đã xảy ra lỗi</h3>
                <div class="alert alert-danger text-start">
                    ${errorMessage != null ? errorMessage : "Hệ thống gặp sự cố ngoài ý muốn!"}
                </div>
                <div class="d-flex justify-content-center gap-2 mt-3">
                    <a href="javascript:history.back()" class="btn btn-secondary">Quay lại</a>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Về danh sách</a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>