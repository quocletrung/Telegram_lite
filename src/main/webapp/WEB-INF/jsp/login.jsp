<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>


<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Login</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f4f4; }
        .container { background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); max-width: 400px; margin: 40px auto; }
        h2 { text-align: center; color: #333; }
        label { display: block; margin-bottom: 8px; color: #555; }
        input[type="text"], input[type="password"] {
            width: calc(100% - 22px); /* Adjust for padding and border */
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        input[type="submit"] {
            background-color: #007bff;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
            font-size: 16px;
        }
        input[type="submit"]:hover { background-color: #0056b3; }
        .error-message { color: red; margin-bottom: 15px; text-align: center; }
        .success-message { color: green; margin-bottom: 15px; text-align: center; }
        .register-link { text-align: center; margin-top: 15px; }
        .register-link a { color: #5cb85c; text-decoration: none; }
        .register-link a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="container">
        <h2>User Login</h2>

        <%-- Hiển thị thông báo thành công (ví dụ: sau khi đăng ký) --%>
        <c:if test="${not empty successMessage}">
            <p class="success-message">${successMessage}</p>
        </c:if>

        <%-- Hiển thị thông báo lỗi nếu có --%>
        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>

        <c:set var="usernameVal" value="${not empty requestScope.username ? requestScope.username : ''}" />

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div>
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" value="<c:out value='${usernameVal}'/>" required>
            </div>
            <div>
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div>
                <input type="submit" value="Login">
            </div>
        </form>
        <div class="register-link">
            <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Register here</a></p>
        </div>
    </div>
</body>
</html>