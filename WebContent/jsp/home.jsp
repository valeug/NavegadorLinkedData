<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Insert title here</title>
	<link rel="stylesheet" type="text/css" href="resources/css/bootstrap.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/bootstrap-theme.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/styles.css" />
</head>
<body>

	<form action="AppServlet" method="get">
		<div id="search-box-home" class="container">		
			<div class="row">
				<p>Ingrese su busqueda:</p>
				<div class="col-md-12">
		            <div class="input-group" id="adv-search">
		                <input name="concept" type="text" class="form-control" placeholder="Search" />
		                <div class="input-group-btn">	                    
	                        <button id="search-button-home" type="submit" class="btn"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
	                    </div>
		                </div>
		            </div>
		          </div>
		        </div>
			</div>
		</div>
	</form>
	
</body>
</html>