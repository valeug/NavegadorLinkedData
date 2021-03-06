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
			                <input id="property-uri-input" name="property-uri" value = "" type="hidden" />
			                <div class="input-group-btn">	                    
		                        <button id="search-button-results" type="submit" class="btn"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
		                    </div>			                
			            </div>
			          
			        </div>			
				</div>
			</form>
			<button style="font-size: 14px; font-weight:bold;" id="confBtn" type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#confModal">Agregar propiedades</button>
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
	<c:if test="${term.properties != null || term.propertyGroups !=null}">
		<div class = "row"> 
			<div style="background-color: #4E4E56; padding: 50px; margin-bottom: 100px;" class="col-md-12 desc-info-row" >
				<p class="subtitle"><c:out value="${term.name}" /></p>
				<p class="subtitle"><c:out value="${term.dataset}" /></p>
				
				<c:if test="${term.associations != null}">
					<div>
						<ul class="list-group">
							
							<c:forEach items="${term.associations}" var="a">
								<li class="list-group-item">
									<p><c:out value="${a.association_name}" /></p>
									<p style="display: none;"><c:out value="${a.association_uri}" /></p>
									
									<p><c:out value="${a.concept_name}" /></p>
									<p style="display: none;"><c:out value="${a.concept_uri}" /></p>
									
									<p><c:out value="${a.origin}" /> - <c:out value="${a.target}" /></p>
									
									<c:if test="${a.inferredAssociations != null}">
										<div>
											<ul class="list-group">												
												<c:forEach items="${a.inferredAssociations}" var="i">
													<li class="list-group-item">
														<p>action: <c:out value="${i.action}" /></p>
														<p>concept: <c:out value="${i.concept_name}" /></p>
													</li>
												</c:forEach>
											</ul>
										</div>
									</c:if>
								</li>
							</c:forEach>
							
						</ul>
					</div>
				</c:if>
				<ul id="prop-list" class="list-group">
					<c:forEach items="${term.properties}" var="t">
						
						<c:if test="${t.value != null}">							
							<c:if test="${t.show_default==1}">
								<div class = "prop-group-elem">	
									<p class="property-name" ><c:out value="${t.name}" /></p>
									<li class="list-group-item show-default">
																			
											<p  class="ref-uri prop-group-uri" style = "display: none;"><c:out value="${t.uri}" /></p>									
											<p style="display: none;"><c:out value="${t.show_default}" /></p>
											<c:if test="${t.uri == 'http://dbpedia.org/ontology/thumbnail'}">
												<img src="${t.value}}" alt="Imagen" >
											</c:if>
											<c:if test="${t.uri != 'http://dbpedia.org/ontology/thumbnail'}">
												<c:if test="${t.is_mapping == 1}">
													<%-- 
													<p><a class="ref-value" href="#" onclick=""><c:out value="${t.value}" /></a></p>
													--%>
													<c:if test="${t.label == null}">
														<div class="prop-label-div">
															<p class="prop-label-1"><a class="ref-value" href="#" onclick=""><c:out value="${t.value}" /></a></p>
														</div>
													</c:if>
													<c:if test="${t.label != null}">
														<div class="prop-label-div">
															<p class="prop-label-2"><a  class="ref-value  prop-label" href="#" onclick=""><c:out value="${t.label}" /></a></p>
															<p style="display:none;"><a class="prop-label-3" ><c:out value="${t.value}" /></a></p>
														</div>
													</c:if>
												</c:if>
												<c:if test="${t.is_mapping != 1}">
													<%-- 
														<p><c:out value="${t.value}" /></p>
													--%>
													<c:if test="${t.label == null}">
														<p><c:out value="${t.value}" /></p>
													</c:if>
													<c:if test="${t.label != null}">
														<p><c:out value="${t.label}" /></p>
													</c:if>	
												</c:if>									
											</c:if>												
									</li>
								</div>
							</c:if>
							<c:if test="${t.show_default==0}">
								<div class = "prop-group-elem" style="display: none;">
									<p class="property-name"><c:out value="${t.name}" /></p>
									<li class="list-group-item not-show-default" style="display: none;">					
									
										<p class="ref-uri prop-group-uri" style = "display: none;"><c:out value="${t.uri}" /></p>
										
										<p><c:out value="${t.name}" /></p>
										<p style="display: none;"><c:out value="${t.show_default}" /></p>
										<c:if test="${t.uri == 'http://dbpedia.org/ontology/thumbnail'}">
											<img src="${t.value}}" alt="Smiley face" >
										</c:if>
										<c:if test="${t.uri != 'http://dbpedia.org/ontology/thumbnail'}">
											<c:if test="${t.is_mapping == 1}">
												<%-- 
												<p><a class="ref-value" href="#" onclick=""><c:out value="${t.value}" /></a></p>
												--%>
												<c:if test="${t.label == null}">
													<div class="prop-label-div">
														<p class="prop-label-1"><a class="ref-value" href="#" onclick=""><c:out value="${t.value}" /></a></p>
													</div>
												</c:if>
												<c:if test="${t.label != null}">
													<div class="prop-label-div">
														<p class="prop-label-2"><a  class="ref-value  prop-label" href="#" onclick=""><c:out value="${t.label}" /></a></p>
														<p style="display:none;"><a class="prop-label-3" ><c:out value="${t.value}" /></a></p>
													</div>
												</c:if>
											</c:if>
											<c:if test="${t.is_mapping != 1}">
												<%-- 
													<p><c:out value="${t.value}" /></p>
												--%>
												<c:if test="${t.label == null}">
													<p><c:out value="${t.value}" /></p>
												</c:if>
												<c:if test="${t.label != null}">
													<p><c:out value="${t.label}" /></p>
												</c:if>	
											</c:if>									
										</c:if>											
									</li>
								</div>
							</c:if>
						</c:if>
					</c:forEach>
				</ul>
				
				<ul id="prop-group-list" class="list-group prop-group-list">
					<c:forEach items="${term.propertyGroups}" var="k">
						<c:if test="${k.show_default==1}">
							<div class = "prop-group-elem">
								<p class="prop-group-name"><c:out value="${k.name}" /></p>								
								<p class="prop-group-uri" style="display: none;"><c:out value="${k.uri}" /></p>
		 						<p style="display: none;"><c:out value="${k.show_default}" /></p>	
		 						
		 						<c:if test="${k.instances==1}">						
									<c:forEach items="${k.propertyList}" var="j"> 
										<li class="list-group-item list-group-instances not-show-default">
												<div>
													<p class="ref-uri" style = "display: none;"><c:out value="${j.uri}" /></p>
													<!-- <p><c:out value="${i.name}" /></p>  -->
													<p style="display: none;"><c:out value="${j.show_default}" /></p>
												</div>
												<c:if test="${j.is_mapping == 1}">
													<%--
													<p><a class="ref-value" href="#" onclick=""><c:out value="${j.value}" /></a></p>
													--%>
													<c:if test="${j.label == null}">
														<div class="prop-label-div">
															<p class="prop-label-4"><a class="ref-value" href="#" onclick=""><c:out value="${j.value}" /></a><span class="glyphicon glyphicon-chevron-right span-icon"></span></p>
														</div>
													</c:if>
													<c:if test="${j.label != null}">
														<div class="prop-label-div">
															<p class="prop-label-5"><a  class="ref-value  prop-label" href="#" onclick=""><c:out value="${j.label}" /></a><span class="glyphicon glyphicon-chevron-right span-icon"></span></p>
															<p style="display:none;"><a class="prop-label-6" ><c:out value="${j.value}" /></a></p>
														</div>
													</c:if>
												</c:if>
												<c:if test="${j.is_mapping != 1}">
													<%-- 
													<p><c:out value="${j.value}" /></p>
													--%>
													<c:if test="${j.label == null}">
														<p><c:out value="${j.value}" /></p>
													</c:if>
													<c:if test="${j.label != null}">
														<p><c:out value="${j.label}" /></p>
													</c:if>	
												</c:if>
											
											</li>
										</c:forEach>
									</c:if>
		 						
		 						<c:if test="${k.instances==0}">						
									<c:forEach items="${k.propertyList}" var="j"> 
										<li class="list-group-item not-show-default">
												<div>
													<p class="ref-uri" style = "display: none;"><c:out value="${j.uri}" /></p>
													<!-- <p><c:out value="${i.name}" /></p>  -->
													<p style="display: none;"><c:out value="${j.show_default}" /></p>
												</div>
												<c:if test="${j.is_mapping == 1}">
													<%--
													<p><a class="ref-value" href="#" onclick=""><c:out value="${j.value}" /></a></p>
													--%>
													<c:if test="${j.label == null}">
														<div class="prop-label-div">
															<p class="prop-label-1"><a class="ref-value" href="#" onclick=""><c:out value="${j.value}" /></a></p>
														</div>
													</c:if>
													<c:if test="${j.label != null}">
														<div class="prop-label-div">
															<p class="prop-label-2"><a  class="ref-value  prop-label" href="#" onclick=""><c:out value="${j.label}" /></a></p>
															<p style="display:none;"><a class="prop-label-3" ><c:out value="${j.value}" /></a></p>
														</div>
													</c:if>
												</c:if>
												<c:if test="${j.is_mapping != 1}">
													<%-- 
													<p><c:out value="${j.value}" /></p>
													--%>
													<c:if test="${j.label == null}">
														<p><c:out value="${j.value}" /></p>
													</c:if>
													<c:if test="${j.label != null}">
														<p><c:out value="${j.label}" /></p>
													</c:if>	
												</c:if>
											
											</li>
										</c:forEach>
									</c:if>
								</div>
						</c:if>
						<c:if test="${k.show_default==0}">
								<div class = "prop-group-elem" style="display: none;">
									
									<p class="prop-group-name"><c:out value="${k.name}" /></p>								
									<p class="prop-group-uri" style="display: none;"><c:out value="${k.uri}" /></p>
			 						<p style="display: none;"><c:out value="${k.show_default}" /></p>	
			 													
									<c:forEach items="${k.propertyList}" var="j"> 
										<li class="list-group-item not-show-default">
											
											<p class="ref-uri" style = "display: none;"><c:out value="${j.uri}" /></p>
											<p style="display: none;"><c:out value="${j.show_default}" /></p>
										
											<c:if test="${j.is_mapping == 1}">
												<%--
												<p><a class="ref-value" href="#" onclick=""><c:out value="${j.value}" /></a></p>
												--%>
												<c:if test="${j.label == null}">
													<div class="prop-label-div">
														<p class="prop-label-1"><a class="ref-value" href="#" onclick=""><c:out value="${j.value}" /></a></p>
													</div>
												</c:if>
												<c:if test="${j.label != null}">
													<div class="prop-label-div">
														<p class="prop-label-2"><a  class="ref-value  prop-label" href="#" onclick=""><c:out value="${j.label}" /></a></p>
														<p style="display:none;"><a class="prop-label-3" ><c:out value="${j.value}" /></a></p>
													</div>
												</c:if>
											</c:if>
											<c:if test="${j.is_mapping != 1}">
												<%-- 
												<p><c:out value="${j.value}" /></p>
												--%>
												<c:if test="${j.label == null}">
													<p><c:out value="${j.value}" /></p>
												</c:if>
												<c:if test="${j.label != null}">
													<p><c:out value="${j.label}" /></p>
												</c:if>	
											</c:if>
										
									</li>
								</c:forEach>
							</div>
						</c:if>					
					</c:forEach>
				</ul>
			</div>
		</div>
	</c:if>	
		
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
		<c:if test="${term.similarTerms != null}">
			<div class = "row"> 
				<div style= "padding: 30px;" class="col-md-12 sim-info-row" >
					<p class="subtitle">Terminos similares:</p> 
					<ul class="list-group">
						<c:forEach items="${term.similarTerms}" var="i">
							<li class="list-group-item">
								<div class="term-list-item">
									<p style="display: none;" ><a href="#" class="term-item-uri" ><c:out value="${i.uri}" /></a></p>
									<p><a href="#" class="term-item-name" ><c:out value="${i.name}" /></a></p>
									<!--  <p><c:out value="${i.definition}" /></p>  -->
								</div>
							</li>
						</c:forEach>
					</ul>
					<div id="mydiv"></div>
				</div>
			</div>
		</c:if>
			
		<c:if test="${instances != null}">
			<div class = "row"> 
				<div class="col-md-12 ref-info-row" style="padding: 50px;">
					<p class="prop-group-name">Instancias de la clase</p>	
					<ul class="list-group">
						<c:forEach items="${instances}" var="i">
							<li class="list-group-item list-element-instance">
								<div class="term-list-item">
									<p class="term-item-uri" style="display: none;"><a><c:out value="${i.uri}" /></a></p>									 
									<p class="term-item-name" style="display: inline-block"><a href="#"><c:out value="${i.name}" /></a></p>
									<div style="display: inline-block" class="flip-instance">
										<span class="glyphicon glyphicon-chevron-down span-icon"></span>
									</div>
									<!--
									<p><c:out value="${i.definition}" /></p>
									-->
									<!-- 
									<div class="flip-instance"><a href="#">Definition</a><span class="glyphicon glyphicon-chevron-down span-icon"></span></div>
									-->
									<div class="panel-comment"><p><c:out value="${i.definition}" /></p></div>
								</div>
							</li>
						</c:forEach>
					</ul>	
					<br>
					<br>
				</div>
			</div>
		</c:if>
	</div>
	
	<script type="text/javascript">
	$(document).ready(function(){
		alert('entro');
		console.log("entro panel slidedown");
		$(".panel-comment").slideUp();
	});
	
	$(".flip-instance").click(function(){
		var div_parent = $(this).parents("div .term-list-item");
		var panel = div_parent.find('.panel-comment');
		panel.slideToggle("slow");
    });
	
	// Get the modal
	var modal = document.getElementById('confModal');
	
	// Get the button that opens the modal
	var btn = document.getElementById("confBtn");
	
	// Get the <span> element that closes the modal
	var span = document.getElementsByClassName("close")[0];
	
	// Get the button that opens the modal
	var btn = document.getElementById("confBtn");
	
	
	/*
		$(".term-item-name").click(function(){
			var div_parent = $(this).parents("div .term-list-item");
		    console.log("div_parent");
			console.log(div_parent);
			var prop_uri = div_parent.find('.term-item-uri');
			console.log("prop_uri");
			console.log(prop_uri.text());
			$("#searchbox").val(prop_uri.text());		
			console.log(prop_uri.text());
		    $("#search-button-results").click();
		});
	
	
	*/
	$("#confBtn").on( "click", function() {
	    modal.style.display = "block";

	    //alert('x1');
	    $("#propertydiv").hide();
	    //$.get('/NavegadorLinkedData/RetrieveOntologies',function(responseJson){
	    //$.get('RetrieveOntologies',function(responseJson){
	    alert('x2');
			//if(responseJson!=null){					
		$("#propertytable").find("tr:gt(0)").remove();
		var table = $("#propertytable");
		//alert('no entro');
		//alert("gg");
		var list = document.getElementById('prop-list');
		console.log("LIST");
		console.log(list);
		console.log("FIN LIST");
		var elements = list.children;
		//var elements = list.find('.prop-group-elem');
		console.log(elements);
		var cont = 1;
		//console.log('for each');
		$.each(elements, function(key, value){
			var row = $("<tr />");
			
			console.log("value: ");
			console.log(value);
			
			var li = value.children[1];
			console.log("li  m1");
			console.log(li);
			//var x = value.find('.prop-group-elem');
			
			var uri = li.children[0].innerHTML; //parrafo uri
			console.log("uri: ");
			console.log(uri);

			var show_default = li.children[1].innerHTML; //parrafo show default
			console.log("show_default: ");
			console.log(show_default);

			
            $("<td />").text(uri).appendTo(row); // URI
            //$("<td />").text(value.children[3]}).appendTo(row); //FALTARIA INDICAR EL NOMBRE DEL DATASET   
           
            if(show_default == 1){
            	//console.log('muestra');
            	var inpStr = '<input type="checkbox" id="'+ cont +'" name="old_checkboxList" checked="checked" />'; 
            	$("<td />").html(inpStr).appendTo(row);
            }
            else {
            	//console.log('no muestra');
            	var inpStr = '<input type="checkbox" id="'+ cont +'" name="new_checkboxList"/>'; 
            	$("<td />").html(inpStr).appendTo(row);
            }
            
            row.appendTo(table);
            cont = cont + 1;
		});		
		
		//prop-group-list
		var list = document.getElementById('prop-group-list');
		//var elementsP = list.getElementsByClassName("prop-group-uri");
		var divs = list.children; //div
		
		console.log('lista agrupadaaaaaa');
		console.log(list);
		console.log("fin");
		
		console.log("elementos");
		console.log(divs);
		console.log("fin");
		
		/*
		//var elementsG = list.getElementsByClassName("list-group-item" );
		var elementsG = list.getElementsByClassName("list-group-item" );
		console.log('lista hijooooos');
		console.log(elementsG);
		*/
		$.each(divs, function(key, value){

			var row = $("<tr />");
			
			console.log("value PROP GROUP");
			console.log(value);
			
			var uri = value.children[1].innerHTML; //parrafo uri
			console.log("uri 2 : ");
			console.log(uri);

			var show_default = value.children[2].innerHTML; //parrafo show default
			console.log("show_default 2 : ");
			console.log(show_default);

			
            $("<td />").text(uri).appendTo(row); // URI
            
            //prueba
            if(show_default == 1){
            	//console.log('muestra');
            	var inpStr = '<input type="checkbox" id="'+ cont +'" name="old_checkboxList" checked="checked" />'; 
            	$("<td />").html(inpStr).appendTo(row);
            }
            else {
            	//console.log('no muestra');
            	var inpStr = '<input type="checkbox" id="'+ cont +'" name="new_checkboxList"/>'; 
            	$("<td />").html(inpStr).appendTo(row);
            }
            
            /*
            var inpStr = '<input type="checkbox" id="'+ cont +'" name="new_checkboxList"/>'; 
            	$("<td />").html(inpStr).appendTo(row);
            */
            
            
            
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
