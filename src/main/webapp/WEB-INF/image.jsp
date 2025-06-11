<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<img 
    src="${pageContext.request.contextPath}/local-images/${profile.picture}" 
    alt="Profile Picture"
    onerror="this.src='${pageContext.request.contextPath}/local-images/default.png'"
/>
</body>
</html>