<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login | Cheep UPF</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500&display=swap" rel="stylesheet">
 	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/login-style.css">
</head>
</head>
<body>
    <div class="login-container">
        <h1>Welcome to Cheep UPF</h1>
        <%-- TODO: Add servlet to handle OAuth2.0 flow --%>
        <%-- <a href="${pageContext.request.contextPath}/GoogleOAuthServlet">
            <button class="google-btn">
                <img class="google-icon" src="${pageContext.request.contextPath}/images/Google_icon.png" width="18" height="18" alt="Google logo">
                
                Sign in with Google
            </button>
        </a> --%>
        <div class="footer">
            Powered by UPF Students
        </div>
    </div>
</body>
</html>

