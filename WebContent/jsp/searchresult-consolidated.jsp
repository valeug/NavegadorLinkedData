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
	          <h4 class="modal-title">Ver propiedades</h4>
	        </div>
	        <div class="modal-body">
	          	<div id="propertydiv" >
					<table class="table" cellspacing="0" id="propertytable">
						<tr>
							<th scope="col">URI</th>
							<th scope="col">Seleccionar</th>
						</tr>
					</table>	    
			   	</div>	
	        </div>
	        <div class="modal-footer">
	          	<button type="submit" class="btn btn-default" id="updatePropBtn">Aceptar</button>
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
			</form>
		</div>
		<!--  
		<div class = "row">	
			<div class="col-md-8 home-page-modal">		
				<button id="confBtn" type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#confModal">Agregar propiedades</button>
			</div>
		</div>
		-->
		<%-- 
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
		--%>
		
		<!--  
		<div class = "row"> 
			<div class="col-md-12 property-info-row" >
				Propiedades:<br> 
			</div>
		</div>
		-->
		
		<div class = "row"> 
			<div style= "padding: 30px;" class="col-md-12 desc-info-row" >			
				<c:forEach items="${termsConsolidated}" var="z">
					<br> URI <br>
					<p>
					<a class="ref-value" href="#" onclick=""><c:out value="${z.uri}" /></a>
					</p>
					
					<p>
					<a class="ref-value" href="#" onclick=""><c:out value="${z.dataset}" /></a>
					</p>
					
					<c:if test="${z.properties != null}">
						<p class="subtitle">Propiedades</p>		
						entro<br>			
						<ul id="prop-list" class="list-group">
							<c:forEach items="${z.properties}" var="i">
								<!-- 
								<a href="#" onclick="myfunction(this);"><c:out value="${i.name}" /></a><br>	
								 -->
								 
								<c:if test="${i.value != null}">
								<%-- --%>
									
									<!-- <c:if test="${i.consolidated == 0}"> no es consolidado</c:if> -->
									<c:if test="${i.consolidated == 1}"> 
											
											<li class="list-group-item show-default">
												
												<p  class="ref-uri" style = "display: none;"><c:out value="${i.uri}" /></p>
														
												<p style = "font-size: 16px; font-weight: bold;" ><c:out value="${i.name}" /></p>
												<p style="display: none;"><c:out value="${i.show_default}" /></p>
												<c:if test="${i.uri == 'http://dbpedia.org/ontology/thumbnail'}">
													<img src="${i.value}}" alt="Smiley face" >
												</c:if>
												<c:if test="${i.uri != 'http://dbpedia.org/ontology/thumbnail'}">
													<c:if test="${i.is_mapping == 1}">
																											
														<c:if test="${i.label == null}">
															<p id="prop-label-1"><a class="ref-value" href="#" onclick=""><c:out value="${i.value}" /></a></p>
														</c:if>
														<c:if test="${i.label != null}">
															<p id="prop-label-2"><a  class="ref-value  prop-label" href="#" onclick=""><c:out value="${i.label}" /></a></p>
															<p style="display:none;"><a id="prop-label-3" ><c:out value="${i.value}" /></a></p>
														</c:if>
														
													</c:if>
													<c:if test="${i.is_mapping != 1}">
														<c:if test="${i.label == null}">
															<p><c:out value="${i.value}" /></p>
														</c:if>
														<c:if test="${i.label != null}">
															<p><c:out value="${i.label}" /></p>
														</c:if>														
													</c:if>									
												</c:if>	
											</li>
										
									</c:if>
								</c:if>
							</c:forEach>	
						</ul>
					</c:if>
					
					<c:if test="${z.propertyGroups != null}">
						<ul id="prop-group-list" class="list-group prop-group-list">
							<!--  <p>PROPIEDADES AGRUPADAS</p> -->
							<c:forEach items="${z.propertyGroups}" var="j">	
								<%-- --%>
								<c:if test="${j.consolidated == 1}"> 
															
										<p class="prop-group-name"><c:out value="${j.name}" /></p>							
										<p class="prop-group-uri"><c:out value="${j.uri}" /></p>	 						
										<c:forEach items="${j.propertyList}" var="k"> 
											<li class="list-group-item not-show-default">
												<p class="ref-uri" style = "display: none;"><c:out value="${k.uri}" /></p>
												<!-- <p><c:out value="${i.name}" /></p>  -->
												<p style="display: none;"><c:out value="${k.show_default}" /></p>
												<c:if test="${k.is_mapping == 1}">													
													<c:if test="${k.label == null}">
														<p id="prop-label-4"><a  class="ref-value" href="#" onclick=""><c:out value="${k.value}" /></a></p>
													</c:if>
													<c:if test="${k.label != null}">
														<p id="prop-label-5"><a  class="ref-value prop-label" href="#" onclick=""><c:out value="${k.label}" /></a></p>
														<p style="display: none;"><a id="prop-label-6"><c:out value="${k.value}" /></a></p>
													</c:if>
												</c:if>
												<c:if test="${k.is_mapping != 1}">													
													<c:if test="${k.label != null}">
														<p><c:out value="${k.label}" /></p>
													</c:if>
													<c:if test="${k.label == null}">
														<p><c:out value="${k.value}" /></p>
													</c:if>
												</c:if>
											</li>
										</c:forEach>		
								</c:if>						
							</c:forEach>
						</ul>
					</c:if>									
					<%-- 
					<!-- PROPIEDADES AGRUPADAS -->
					<c:if test="${i.propertyGroups==null}">
						is null kid !!!!!!!!!!!!!!!!!
					</c:if>
					<c:if test="${i.propertyGroups!=null}">
						<c:forEach items="${i.propertyGroups}" var="i">	
							<!-- 						
							<p class="prop-group-name"><c:out value="${i.name}" /></p>								
							<p class="prop-group-uri"><c:out value="${i.uri}" /></p>
							 -->		 						
							Propiedades (agrupados)
							<ul id="prop-list" class="list-group">
								<c:forEach items="${i.propertyList}" var="i"> 
									<li class="list-group-item not-show-default">
										<p class="ref-uri" style = "display: none;"><c:out value="${i.uri}" /></p>
										<!-- <p><c:out value="${i.name}" /></p>  -->
										<p style="display: none;"><c:out value="${i.show_default}" /></p>
										<c:if test="${i.is_mapping == 1}">
											<p><a class="ref-value" href="#" onclick=""><c:out value="${i.value}" /></a></p>
										</c:if>
										<c:if test="${i.is_mapping != 1}">
											<p><c:out value="${i.value}" /></p>
										</c:if>
										XD
									</li>
								</c:forEach>
							</ul>					
						</c:forEach>
					</c:if>
					--%>
					
					

				</c:forEach>				
			</div>
		</div>
		<%--
		<div class = "row"> 
			<div style= "padding: 30px;" class="col-md-12 desc-info-row" >
				<p class="subtitle">Propiedades</p>
				<ul id="prop-list" class="list-group">
					<c:forEach items="${term.properties}" var="i">
						<!-- 
						<a href="#" onclick="myfunction(this);"><c:out value="${i.name}" /></a><br>	
						 -->
						<c:if test="${i.value != null}">
							<c:if test="${i.show_default==1}">
								<li class="list-group-item show-default">
									
									<p  class="ref-uri" style = "display: none;"><c:out value="${i.uri}" /></p>
											
									<p style = "font-size: 16px; font-weight: bold;" ><c:out value="${i.name}" /></p>
									<p style="display: none;"><c:out value="${i.show_default}" /></p>
									<c:if test="${i.uri == 'http://dbpedia.org/ontology/thumbnail'}">
										<img src="${i.value}}" alt="Smiley face" >
									</c:if>
									<c:if test="${i.uri != 'http://dbpedia.org/ontology/thumbnail'}">
										<c:if test="${i.is_mapping == 1}">
											<p><a class="ref-value" href="#" onclick=""><c:out value="${i.value}" /></a></p>
										</c:if>
										<c:if test="${i.is_mapping != 1}">
											<p><c:out value="${i.value}" /></p>
										</c:if>									
									</c:if>		
								</li>
							</c:if>
							<c:if test="${i.show_default==0}">
								<li class="list-group-item not-show-default" style="display: none;">						
									
									<p class="ref-uri" style = "display: none;"><c:out value="${i.uri}" /></p>
									
									<p><c:out value="${i.name}" /></p>
									<p style="display: none;"><c:out value="${i.show_default}" /></p>
									<c:if test="${i.uri == 'http://dbpedia.org/ontology/thumbnail'}">
										<img src="${i.value}}" alt="Smiley face" >
									</c:if>
									<c:if test="${i.uri != 'http://dbpedia.org/ontology/thumbnail'}">
										<c:if test="${i.is_mapping == 1}">
											<p><a class="ref-value" href="#" onclick=""><c:out value="${i.value}" /></a></p>
										</c:if>
										<c:if test="${i.is_mapping != 1}">
											<p><c:out value="${i.value}" /></p>
										</c:if>									
									</c:if>		
								</li>
							</c:if>
						</c:if>
					</c:forEach>
				</ul>
				<ul id="prop-group-list" class="list-group prop-group-list">
					<c:forEach items="${term.propertyGroups}" var="i">
						
						<p class="prop-group-name"><c:out value="${i.name}" /></p>
						
						<p class="prop-group-uri"><c:out value="${i.uri}" /></p>
 						
						Propiedades (valores)
						<c:forEach items="${i.propertyList}" var="i"> 
							<li class="list-group-item not-show-default">
								<p class="ref-uri" style = "display: none;"><c:out value="${i.uri}" /></p>
								<!-- <p><c:out value="${i.name}" /></p>  -->
								<p style="display: none;"><c:out value="${i.show_default}" /></p>
								<c:if test="${i.is_mapping == 1}">
									<p><a class="ref-value" href="#" onclick=""><c:out value="${i.value}" /></a></p>
								</c:if>
								<c:if test="${i.is_mapping != 1}">
									<p><c:out value="${i.value}" /></p>
								</c:if>
							</li>
						</c:forEach>
						---------------------------						
					</c:forEach>
				</ul>
			</div>
		</div>
		
		 --%>
		
		
		<%-- 
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
		--%>
		
		<%--
		<div class = "row"> 
			<div style= "padding: 30px;" class="col-md-12 sim-info-row" >
				<p class="subtitle">Terminos similares:</p> 
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
		--%>
	</div>
	
	<script type="text/javascript">
	
	// Get the modal
	var modal = document.getElementById('confModal');
	
	// Get the button that opens the modal
	var btn = document.getElementById("confBtn");
	
	// Get the <span> element that closes the modal
	var span = document.getElementsByClassName("close")[0];
	
	// Get the button that opens the modal
	var btn = document.getElementById("confBtn");
	
	$("#confBtn").on( "click", function() {
	    modal.style.display = "block";
	    //
	    //alert('x1');
	    $("#propertydiv").hide();
	    //$.get('/NavegadorLinkedData/RetrieveOntologies',function(responseJson){
	    //$.get('RetrieveOntologies',function(responseJson){
	    	//alert('x2');
			//if(responseJson!=null){					
				$("#propertytable").find("tr:gt(0)").remove();
				var table = $("#propertytable");
				//alert('no entro');
				//alert("gg");
				var list = document.getElementById('prop-list');
				console.log(list);
				var elements = list.children;
				console.log(elements);
				var cont = 1;
				console.log('for each');
				$.each(elements, function(key, value){
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
					console.log(value);
					//console.log('p');
					//console.log(value.children[0].innerHTML);
					console.log(value.children[0]);
					//alert(value.children[0].innerHTML)
                    $("<td />").text(value.children[0].innerHTML).appendTo(row); // URI
                    //$("<td />").text(value.children[3]}).appendTo(row); //FALTARIA INDICAR EL NOMBRE DEL DATASET   
                    var show_default = value.children[2].innerHTML;
                    if(show_default == 1){
                    	console.log('muestra');
                    	var inpStr = '<input type="checkbox" id="'+ cont +'" name="old_checkboxList"/ checked="checked">'; 
                    	$("<td />").html(inpStr).appendTo(row);
                    }
                    else {
                    	console.log('no muestra');
                    	var inpStr = '<input type="checkbox" id="'+ cont +'" name="new_checkboxList"/>'; 
                    	$("<td />").html(inpStr).appendTo(row);
                    }
                    
                    row.appendTo(table);
                    cont = cont + 1;
				});		
				
				//prop-group-list
				var list = document.getElementById('prop-group-list');
				var elementsP = list.getElementsByClassName("prop-group-uri");
				
				console.log('lista agrupadaaaaaa');
				console.log(elementsP);
				/*
				//var elementsG = list.getElementsByClassName("list-group-item" );
				var elementsG = list.getElementsByClassName("list-group-item" );
				console.log('lista hijooooos');
				console.log(elementsG);
				*/
				$.each(elementsP, function(key, value){

					var row = $("<tr />");
					console.log(value);
					
                    $("<td />").text(value.innerHTML).appendTo(row); // URI
                    /*
                    var show_default = value.children[2].innerHTML;
                    if(show_default == 1){
                    	console.log('muestra');
                    	var inpStr = '<input type="checkbox" id="'+ cont +'" name="old_checkboxList"/ checked="checked">'; 
                    	$("<td />").html(inpStr).appendTo(row);
                    }
                    else {
                    	console.log('no muestra');
                    	var inpStr = '<input type="checkbox" id="'+ cont +'" name="new_checkboxList"/>'; 
                    	$("<td />").html(inpStr).appendTo(row);
                    }
                   	*/
                    //prueba
                    var inpStr = '<input type="checkbox" id="'+ cont +'" name="new_checkboxList"/>'; 
                    	$("<td />").html(inpStr).appendTo(row);
                    	
                    row.appendTo(table);
                    cont = cont + 1;
				});
			//}			
		//});
		$("#propertydiv").show();
		
	});
	</script>
</body>
</html>
