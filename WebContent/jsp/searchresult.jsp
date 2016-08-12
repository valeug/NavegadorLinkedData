<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Insert title here</title>
	<link rel="stylesheet" type="text/css" href="resources/css/bootstrap.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/bootstrap-theme.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/styles.css" />
	<script language="JavaScript" type="text/javascript" src="resources/js/jquery-3.1.0.min.js"></script>
	<script type="text/javascript" src="resources/js/main.js"></script>
</head>
<body>
	
	<form action="AppServlet" method="get">
		<div id="search-box-results" class="container">		
			<div class="row">
				<div class="result-col">
		            <div class="input-group" id="adv-search">
		                <input id="searchbox" name="concept" type="text" class="form-control" placeholder="Search" />
		                <div class="input-group-btn">	                    
	                        <button id="search-button-results" type="submit" class="btn"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
	                    </div>
		                </div>
		            </div>
		          </div>
		        </div>
			</div>
		</div>
	</form>
	
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
	<form action="AppServlet" method="post">
		<c:forEach items="${term.similarTerms}" var="i">
			<!-- 
			<a href="#" onclick="myfunction(this);"><c:out value="${i.name}" /></a><br>	
			 -->
			<a href="#" class="term" ><c:out value="${i.name}" /></a><br>
		</c:forEach>
	</form>
	<br>
	<br>
	Terminos enlazados:<br> 
	<c:forEach items="${term.linkedTerms}" var="i">
		<c:out value="${i.name}" /><br>
	</c:forEach>
	<div id="mydiv"></div>
	
</body>
</html>
