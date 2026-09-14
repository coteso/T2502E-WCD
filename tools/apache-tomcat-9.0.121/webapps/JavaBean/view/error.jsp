<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lỗi</title>
</head>
<body>
    <h1>Có lỗi xảy ra</h1>
    <p style="color:red;">
        ${error}
    </p>
    <p><a href="${pageContext.request.contextPath}/products">Quay lại danh sách sản phẩm</a></p>
</body>
</html>