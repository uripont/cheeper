<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Login | Cheep UPF</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500&display=swap" rel="stylesheet">
 	<link rel="stylesheet" href="<%= contextPath %>/static/css/login-style.css">
</head>
</head>
<body>
    <div class="page-wrapper">
        <div class="logo-heading">
            <img class="background-logo" src="<%= contextPath %>/static/images/white-logo.png" alt="Cheep UPF Logo">
            <span class="logo-text">heeper</span>
        </div>

        
        <div class="login-container">
            <h1>Welcome to Cheep UPF</h1>
            <a href="<%= contextPath %>/auth/google-login">
                <button class="google-btn">
                    <img class="google-icon" src="<%= contextPath %>/static/images/google-icon.png" width="18" height="18" alt="Google logo">
                    Sign in with Google
                </button>
            </a>
            <div class="footer">
                Powered by UPF Students
            </div>
        </div>
    </div>
</body>

</html>

