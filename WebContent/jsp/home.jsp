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
	  <div class="modal fade" id="myModal" role="dialog">
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
		
	<a id="conf-link" href="/NavegadorLinkedData/Configuration">Configuracion avanzada</a>
	<!-- CONTENT -->
	<div class ="center-vertical">
		
		<div class="container">
		
			<!-- 
			<form action="Configuration" method="get">
				<div class = "row">	
					<div class="col-md-8 home-page-modal">		
						
						<input id="advanced-confBtn" type="submit" value="Configuracion avanzada"/>						
					</div>
				</div>
			</form>
			 -->
			<form action="AppServlet" method="get">
				<div class = "row">			
					<div class="col-md-8 home-page-type">	
						<p style="font-size: 15px; font-weight: bold;">	Tipo de busqueda</p>				
						<label class="radio-inline"><input type="radio" name="optradio" value="1" checked="checked">Nombre exacto</label>
						<label class="radio-inline"><input type="radio" name="optradio" value="2">Coincidencia en nombre</label>
						<label class="radio-inline"><input type="radio" name="optradio" value="3">Coincidencia en propiedades</label>
					</div>
				</div>
				<div class = "row">				
					<div id="search-box-home" class="col-md-8 home-page-search">					
			            <div class="input-group" id="adv-search">
			                <input name="concept" type="text" class="form-control" placeholder="Search" style = "height: 40px"/>
			                <div class="input-group-btn">	                    
		                        <button id="search-button-home" type="submit" class="btn"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
		                    </div>		                
			            </div>		         
			        </div>					
				</div>
			</form>
			<div class = "row">	
				<div class="col-md-8 home-page-modal">		
					<button id="myBtn" type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#myModal">Seleccionar fuentes</button>
				</div>
			</div>
			
		</div>
	</div>
	
	
	
	
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
		    	//alert('x2');
				if(responseJson!=null){					
					$("#ontologytable").find("tr:gt(0)").remove();
					var table = $("#ontologytable");
					//alert('no entro');
					//alert("gg");
					//var cont = 1;
					
					$.each(responseJson, function(key, value){
						console.log(value);
						/*
						var rowNew = $("<tr><td></td><td></td></tr>");
						alert(value['name']);
						rowNew.children().eq(0).text(value['name']);
						rowNew.children().eq(1).text(value['description']);
						rowNew.appendTo(table);
						*/
						//alert('entro each');
						//alert(value['name']);
						var row = $("<tr />");
                        $("<td />").text(value['name']).appendTo(row);
                        $("<td />").text(value['description']).appendTo(row);
                        //var inpStr = '<input type="checkbox" id="'+ cont +'" name="checkboxList"/>'; 
                        console.log("id: " + value['id']);
                        var inpStr = '<input type="checkbox" id="'+ value['id'] +'" name="checkboxList"/>'; 
                        $("<td />").html(inpStr).appendTo(row);
                        row.appendTo(table);
                        //cont = cont + 1;
					});					
					//alert('wp');
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