<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Registration</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f4f4; }
        .container { background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); max-width: 400px; margin: 40px auto; }
        h2 { text-align: center; color: #333; }
        label { display: block; margin-bottom: 8px; color: #555; }
        input[type="text"], input[type="password"], input[type="email"] {
            width: calc(100% - 22px); /* Adjust for padding and border */
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        input[type="submit"] {
            background-color: #5cb85c;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
            font-size: 16px;
        }
        input[type="submit"]:hover { background-color: #4cae4c; }
        .error-message { color: red; margin-bottom: 15px; text-align: center; }
        .login-link { text-align: center; margin-top: 15px; }
        .login-link a { color: #007bff; text-decoration: none; }
        .login-link a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="container">
        <h2>User Registration</h2>

        <%-- Hiển thị thông báo lỗi nếu có --%>
        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>

        <%--
            Lấy lại các giá trị đã nhập nếu có lỗi và form được load lại.
            Giá trị được lấy từ request attributes mà servlet đã set.
        --%>
        <c:set var="usernameVal" value="${not empty requestScope.username ? requestScope.username : ''}" />
        <c:set var="displayNameVal" value="${not empty requestScope.displayName ? requestScope.displayName : ''}" />
        <c:set var="emailVal" value="${not empty requestScope.email ? requestScope.email : ''}" />

        <form action="${pageContext.request.contextPath}/register" method="post">
            <div>
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" value="<c:out value='${usernameVal}'/>" required>
            </div>
            <div>
                <label for="displayName">Display Name:</label>
                <input type="text" id="displayName" name="displayName" value="<c:out value='${displayNameVal}'/>" required>
            </div>
            <div>
                <label for="email">Email (Optional):</label>
                <input type="email" id="email" name="email" value="<c:out value='${emailVal}'/>">
            </div>
            <div>
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div>
                <label for="confirmPassword">Confirm Password:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>
            <div>
                <input type="submit" value="Register">
            </div>
        </form>
        <div class="login-link">
            <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Login here</a></p>
        </div>
    </div>
</body>
</html>