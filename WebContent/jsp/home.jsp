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
	<!-- Trigger/Open The Modal -->
	<button id="myBtn">Open Modal</button>
	<!-- Modal -->
	<div id="myModal" class="modal" style="height:200px;">
	
		  <!-- Modal content -->
		  <div class="modal-content" style="height:200px;">
			    <span class="close">x</span>
			    <div id="ontologydiv" style="height:100px;">
					<table cellspacing="0" id="ontologytable">
						<tr>
							<th scope="col">Name</th>
							<th scope="col">Description</th>
							<th scope="col">Select</th>
						</tr>
					</table>	    
			   	</div>
			    
			   <!--
			    <c:forEach items="${ontologias}" var="i">			
				    <div class="checkbox">
					  <label><input type="checkbox" value=""><c:out value="${i.name}" /></label>
					</div>
				</c:forEach>
				 -->			  
		    <button id="ontoButton">Aceptar</button>
		  </div>
	
	</div>
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
	
	
	
	<script>
		// Get the modal
		var modal = document.getElementById('myModal');
		
		// Get the button that opens the modal
		var btn = document.getElementById("myBtn");
		
		// Get the <span> element that closes the modal
		var span = document.getElementsByClassName("close")[0];
		
		// When the user clicks the button, open the modal
		btn.onclick = function() {
		    modal.style.display = "block";
		    //
		    //alert('x1');
		    $("#ontologydiv").hide();
		    //$.get('/NavegadorLinkedData/RetrieveOntologies',function(responseJson){
		    $.get('RetrieveOntologies',function(responseJson){
		    	alert('x2');
				if(responseJson!=null){					
					$("#ontologytable").find("tr:gt(0)").remove();
					var table = $("#ontologytable");
					alert('no entro');
					alert("gg");
					$.each(responseJson, function(key, value){
						/*
						var rowNew = $("<tr><td></td><td></td></tr>");
						alert(value['name']);
						rowNew.children().eq(0).text(value['name']);
						rowNew.children().eq(1).text(value['description']);
						rowNew.appendTo(table);
						*/
						var row = $("<tr />");
                        $("<td />").text(value['name']).appendTo(row);
                        $("<td />").text(value['description']).appendTo(row);
                        $("<td />").html('<input type="checkbox"/>').appendTo(row);
                        row.appendTo(table);
					});			
					
					alert('wp');
				}			
			});
			$("#ontologydiv").show();
			
		}
		
		// When the user clicks on <span> (x), close the modal
		span.onclick = function() {
		    modal.style.display = "none";
		}
		
		// When the user clicks anywhere outside of the modal, close it
		window.onclick = function(event) {
		    if (event.target == modal) {
		        modal.style.display = "none";
		    }
		}
	</script>
	
</body>
</html>