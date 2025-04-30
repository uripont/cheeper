<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome</title>
</head>
<body>
    <h1>Welcome, <%= session.getAttribute("name") %>!</h1>
    <p>Your email is: <%= session.getAttribute("email") %></p>
</body>
</html>
<!-- TODO: Work on registration flows according to user type -->