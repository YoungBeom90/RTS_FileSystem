<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Spring Boot Test</title>
		</head>
	<body>
	<%
	    out.println("JdbcTemplate : Hello World");
	%>
	<br>
	<c:forEach var="vo" items="${list}">
	    아이디 : ${vo.user_id} / 주소 : ${vo.address} / 가입일 : ${vo.signup_dt}<br>
	</c:forEach>
	</body>
</html>