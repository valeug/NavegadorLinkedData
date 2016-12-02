<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Linked data navigator</title>
	<link rel="stylesheet" type="text/css" href="resources/css/bootstrap.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/bootstrap-theme.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/bootstrap.min.css" /> 
	<link rel="stylesheet" type="text/css" href="resources/css/styles.css" />
	
	<!--
	<script language="JavaScript" type="text/javascript" src="resources/js/jquery-3.1.0.min.js"></script>
	-->
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script  type="text/javascript" src="resources/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="resources/js/main.js"></script>
	
</head>
<body>
		
	  <!-- Modal -->
	  <div class="modal fade" id="confModal" role="dialog">
	    <div class="modal-dialog modal-lg">
	    
	      <!-- Modal content-->
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal">&times;</button>
	          <h4 class="modal-title">Seleccionar fuentes de los datos</h4>
	        </div>
	        <div class="modal-body">
	          	<div id="ontologydiv" >
					<table class="table" cellspacing="0" id="ontologytable">
						<tr>
							<th scope="col">Name</th>
							<th scope="col">Description</th>
							<th scope="col">Select</th>
						</tr>
					</table>	    
			   	</div>	
	        </div>
	        <div class="modal-footer">
	          	<button type="submit" class="btn btn-default" id="saveOntoBtn">Aceptar</button>
	          	<button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
	        </div>
	      </div>
	      
	    </div>
	  </div>
	<div class="container">
		<div class="row">
			<form action="AppServlet" method="get">
				<div id="search-box-results" class="container">
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
			</form>
		</div>
		<div class = "row">	
			<div class="col-md-8 home-page-modal">		
				<button id="confBtn" type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#confModal">Agregar propiedades</button>
			</div>
		</div>
		<div class = "row"> 
			<div class="col-md-12 term-info-row" >
				Term: ${term.name}<br>
				<!--  
				Description: ${term.definition}<br>
				-->
				<!-- 
				<c:forEach var="i" begin="1" end="5" step="1">
					<c:out value="${i}" />
				</c:forEach>
				 -->
				<br>
				<br>
			</div>
		</div>
		<!--  
		<div class = "row"> 
			<div class="col-md-12 property-info-row" >
				Propiedades:<br> 
			</div>
		</div>
		-->
		<div class = "row"> 
			<div class="col-md-12 desc-info-row" >
				Descriptivas<br>
				<ul class="list-group">
					<c:forEach items="${term.properties}" var="i">
						<!-- 
						<a href="#" onclick="myfunction(this);"><c:out value="${i.name}" /></a><br>	
						 -->
						<c:if test="${i.value != null}">
							<c:if test="${i.show_default==1}">
								<li class="list-group-item">						
									uri:<c:out value="${i.uri}" /><br>
									nombre:<c:out value="${i.name}" /><br>
									<c:if test="${i.uri == 'http://dbpedia.org/ontology/thumbnail'}">
										<img src="${i.value}}" alt="Smiley face" >
									</c:if>
									<c:if test="${i.uri != 'http://dbpedia.org/ontology/thumbnail'}">
										<c:if test="${i.is_mapping == 1}">
											<a href="#" onclick=""><c:out value="${i.value}" /></a>
										</c:if>
										<c:if test="${i.is_mapping != 1}">
											<c:out value="${i.value}" />
										</c:if>									
									</c:if>		
								</li>
							</c:if>
						</c:if>
					</c:forEach>
				</ul>
			</div>
		</div>
		<div class = "row"> 
			<div class="col-md-12 ref-info-row" >
				Referenciables<br>
				<!--  
				<c:forEach items="${term.properties}" var="i">
					<a href="#" class="term" ><c:out value="${i.uri}" /></a><br>
					<a href="#" class="term" ><c:out value="${i.name}" /></a><br>
					<a href="#" class="term" ><c:out value="${i.value}" /></a><br>
				</c:forEach>
				-->			
				<br>
				<br>
			</div>
		</div>
		<div class = "row"> 
			<div class="col-md-12 en-info-row" >
				Terminos enlazados:<br> 
				<c:forEach items="${term.linkedTerms}" var="i">
					<!--   
					<c:out value="${i.name}" /><br>
					<a href="#" class="term" ><c:out value="${i.uri}" /></a><br>
					-->
					<li class="list-group-item">
                         <a href="#" style="display: none;" class="linked-t-uris" ><c:out value="${i.uri}" /><br></a>
                         <a href="#" class="term" ><c:out value="${i.name}" /></a><br>
                    </li>
				</c:forEach>
				<div id="mydiv"></div>
				<br>
				<br>
			</div>
		</div>
		<div class = "row"> 
			<div class="col-md-12 sim-info-row" >
				Terminos similares:<br> 
				<ul class="list-group">
					<c:forEach items="${term.similarTerms}" var="i">
						<li class="list-group-item">
							<a href="#" style="display: none;" class="linked-t-uris" ><c:out value="${i.uri}" /><br></a>
							<a href="#" class="term" ><c:out value="${i.name}" /></a><br>
						</li>
					</c:forEach>
				</ul>
				<div id="mydiv"></div>
			</div>
		</div>
	</div>
</body>
</html>
