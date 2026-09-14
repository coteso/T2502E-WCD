<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Dang nhap - MVC Product Demo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="login-wrapper">
        <div class="login-box">
            <h1>Dang nhap he thong</h1>
            <p class="subtitle">MVC Demo &middot; JSP + Servlet + JavaBean + MySQL</p>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">${error}</div>
            <% } %>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label for="username">Ten dang nhap</label>
                    <input type="text" id="username" name="username"
                           value="${username != null ? username : ''}" required autofocus>
                </div>
                <div class="form-group">
                    <label for="password">Mat khau</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-block">Dang nhap</button>
            </form>

            <p style="margin-top:16px; font-size:12px; color:#6b7280; text-align:center;">
                Tai khoan demo: <strong>admin</strong> / <strong>admin123</strong>
            </p>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
