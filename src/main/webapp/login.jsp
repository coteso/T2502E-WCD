<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Nhập | ORM Product System</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg-dark: #0f172a;
            --card-bg: #1e293b;
            --card-border: #334155;
            --primary: #3b82f6;
            --primary-hover: #2563eb;
            --text-light: #f8fafc;
            --text-dim: #94a3b8;
            --danger: #ef4444;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Inter', sans-serif;
        }

        body {
            background-color: var(--bg-dark);
            color: var(--text-light);
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            padding: 20px;
        }

        .login-card {
            background-color: var(--card-bg);
            border: 1px solid var(--card-border);
            border-radius: 16px;
            padding: 40px;
            max-width: 420px;
            width: 100%;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
        }

        .login-header {
            text-align: center;
            margin-bottom: 28px;
        }

        .login-title {
            font-size: 24px;
            font-weight: 700;
            color: #60a5fa;
            margin-bottom: 8px;
        }

        .login-subtitle {
            font-size: 13px;
            color: var(--text-dim);
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            font-size: 13px;
            font-weight: 500;
            color: var(--text-dim);
            margin-bottom: 6px;
        }

        input[type="text"], input[type="password"] {
            width: 100%;
            background-color: #0f172a;
            border: 1px solid var(--card-border);
            border-radius: 8px;
            padding: 12px 14px;
            color: var(--text-light);
            font-size: 14px;
            outline: none;
            transition: border-color 0.2s;
        }

        input[type="text"]:focus, input[type="password"]:focus {
            border-color: var(--primary);
        }

        .btn {
            width: 100%;
            padding: 12px;
            background-color: var(--primary);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.2s;
            margin-top: 8px;
        }

        .btn:hover {
            background-color: var(--primary-hover);
        }

        .alert {
            padding: 12px;
            border-radius: 8px;
            font-size: 13px;
            margin-bottom: 20px;
        }

        .alert-error {
            background: rgba(239, 68, 68, 0.15);
            border: 1px solid rgba(239, 68, 68, 0.4);
            color: #fca5a5;
        }

        .alert-info {
            background: rgba(59, 130, 246, 0.15);
            border: 1px solid rgba(59, 130, 246, 0.4);
            color: #93c5fd;
        }

        .demo-credentials {
            margin-top: 24px;
            padding-top: 16px;
            border-top: 1px solid var(--card-border);
            font-size: 12px;
            color: var(--text-dim);
            text-align: center;
        }
    </style>
</head>
<body>

    <div class="login-card">
        <div class="login-header">
            <h1 class="login-title">🔑 Đăng Nhập Hệ Thống</h1>
            <p class="login-subtitle">Quản Lý Sản Phẩm JPA / Hibernate ORM</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-error"><c:out value="${error}" /></div>
        </c:if>

        <c:if test="${param.error eq 'need_login'}">
            <div class="alert alert-error">🔒 Vui lòng đăng nhập để tiếp tục.</div>
        </c:if>

        <c:if test="${param.msg eq 'logged_out'}">
            <div class="alert alert-info">ℹ️ Bạn đã đăng xuất thành công.</div>
        </c:if>

        <form action="<c:url value='/login' />" method="post">
            <div class="form-group">
                <label for="username">Tên đăng nhập</label>
                <input type="text" id="username" name="username" value="<c:out value='${username}' />" required autofocus placeholder="Nhập tên đăng nhập...">
            </div>

            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" required placeholder="Nhập mật khẩu...">
            </div>

            <button type="submit" class="btn">Đăng Nhập</button>
        </form>

        <div class="demo-credentials">
            Tài khoản dùng thử: <strong>admin</strong> / <strong>admin123</strong>
        </div>
    </div>

</body>
</html>
