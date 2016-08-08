<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script language="JavaScript" type="text/javascript" src="resources/js/jquery-3.1.0.min.js"></script>
<script type="text/javascript" src="resources/js/main.js"></script>
</head>
<body>
	it worked!
	<br>
	Term: ${term.name}<br>
	Description: ${term.definition}<br>
	<!-- 
	<c:forEach var="i" begin="1" end="5" step="1">
		<c:out value="${i}" />
	</c:forEach>
	 -->
	<br>
	<br>
	Terminos Similares:<br> 
	<c:forEach items="${term.similarTerms}" var="i">
		<a href="#" onclick="myfunction(this);"><c:out value="${i.name}" /></a><br>
	</c:forEach>
	<br>
	<br>
	Terminos enlazados:<br> 
	<c:forEach items="${term.linkedTerms}" var="i">
		<c:out value="${i.name}" /><br>
	</c:forEach>
</body>
</html>
