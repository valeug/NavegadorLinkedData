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
					 	
	<div class="tabbable"> <!-- Only required for left/right tabs -->
		<ul class="nav nav-tabs" style="background-color: #ADD9E4; border:0;">
			<li class="active"><a href="#tab1" data-toggle="tab">Propiedades</a></li>
			<li><a href="#tab2" data-toggle="tab">Agregar nueva propiedad</a></li>
	    		    	
	  	</ul>
	  	<div class="tab-content">	
	  		<div id="tab1" class="tab-pane fade in active">
				<div class="container" style="margin-top:100px;">
		       		<div class="form-group">
						<label for="dataset-cons-sel">Seleccionar dataset</label>
						<select name="dataset" id="dataset-Prop" class="form-control"></select>
					</div>
					<div class="form-group">
						<label for="class-cons-sel">Seleccionar clase</label>
						<select name="class" id="class-Prop" class="form-control"></select>
					</div>
					
					<div class = "form-group">	
						<label for="class-cons-sel">Tabla de propiedades</label>
			         	<div id="propertydiv" style="background-color:white;">
							<table class="table table-p" cellspacing="0" id="propertytable">
								<tr id="propertytable-head" class="table-head">
									<th scope="col">Name</th>
									<th scope="col">Description</th>
									<th scope="col">Consolidated</th>
								</tr>
							</table>	    
				   		</div>	
			   		</div>
			   		<div style="margin-top:50px;">
						<button style = "margin-right: 20px; " type="submit" class="btn btn-primary" id="savePropBtn">Guardar</button>
		         		<button type="button" class="btn btn-primary" data-dismiss="modal">Salir</button>
					</div>
		       	</div>
		    </div>  		
	    	<div id="tab2" class="tab-pane fade" >
	      		<form action="Configuration" method="post">
					<div class="container" style="margin-top:100px;">
						
						<!--  
						<div class = "row">	
							<div class="col-md-8 home-page-modal">		
								<button id="myBtn-prop" type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#myPropModal">Propiedades consolidadas</button>
							</div>
						</div>
						-->
										
						<div class="form-group">
							<label for="dataset-sel">Seleccionar dataset</label>
							<select name="dataset" id="dataset" class="form-control">
							</select>
						</div>
						<div class="form-group">
							<label for="class-sel">Seleccionar clase</label>
							<select name=class id="class" class="form-control">
							</select>
						</div>	
						<div class="form-group">
						    <label for="uri-prop">Ingresar URI de la propiedad:</label>
						    <input type="text" class="form-control" id="uriInputProperty" name="uriInputProperty" placeholder="URI">		
						</div>
						<div class="form-group">
						    <label for="name-prop">Ingresar nombre de la propiedad:</label>
						    <input type="text" class="form-control" id="nameInputProperty" name="nameInputProperty" placeholder="Nombre">		
						</div>
						<div class="form-group">
						    <label for="desc-prop">Ingresar descripción de la propiedad:</label>
						    <input type="text" class="form-control" id="descriptionInputProperty" name="descriptionInputProperty" placeholder="Descripcion">		
						</div>
						<div class="form-check">
			    			<label class="form-check-label">Mapeo</label>
			      			<input id = "checkbox-mapping" name="checkbox-mapping" class="form-check-input" type="checkbox">
						</div>
						<div class="form-group">
							<label for="dataset-sel">Seleccionar dataset objetivo</label>
							<select name="datasetMapping" id="datasetMapping" class="form-control">
							</select>
						</div>
						<div class="form-check">
			    			<label class="form-check-label">Obtener instancias</label>
			      			<input id = "checkbox-mapping" name="checkbox-mapping" class="form-check-input" type="checkbox">
						</div>
						<!--
						<div class="form-group">
						    <label for="desc-prop">Ingresar valor a otro dataset</label>
						    <input type="text" class="form-control" id="mappingInputProperty" name="mappingInputProperty" placeholder="URI o código">		
						</div>
						-->
						<div style="margin-top:50px;">
							<button style = "margin-right: 20px; " type="submit" class="btn btn-primary">Guardar</button>
							<a href="/NavegadorLinkedData" class="btn btn-primary">Salir</a>
						</div>
							
					</div>	
				</form>
	    	</div>
	  		
	  	</div>
	</div>
	
	
	

	<script type="text/javascript">
	$( document ).ready(function() {
		
			/*	TAB 2 CONSOLIDATED */

			$("#propertydiv").hide();
		    //$.get('/NavegadorLinkedData/RetrieveOntologies',function(responseJson){

		    $.get('Configuration/Property/Update',function(responseJson){
		    //$.get('UpdateProperty',function(responseJson){
		    	
		    	//alert('UpdateProperty x2');
				if(responseJson!=null){					
					$("#propertytable").find("tr:gt(0)").remove();
					var table = $("#propertytable");
					//alert('no entro');
					//alert("UpdateProperty gg");
					var cont = 1;					
					//alert(responseJson);
					
					console.log(responseJson);
					$.each(responseJson, function(key, value){
						
						var class_dataset = "dataset-" + value['dataset'];
	                    var class_class = "class-" + value['id_class'];
	                    	                    
						var row = $('<tr class="'+ class_dataset + ' ' + class_class +'" />');
	                    $("<td />").text(value['uri']).appendTo(row);
	                    $("<td />").text(value['name']).appendTo(row);
	                    
	                    	                    
	                    var inpStr = '<input type="checkbox" class="' + class_dataset + ' ' + class_class + '" id="'+ value['id'] +'" name="checkboxPList"/>'; 
	                    
	                    if(value['consolidated']==1){
	                    	var inpStr = '<input type="checkbox" class="' + class_dataset + ' ' + class_class + '" id="'+ value['id'] +'" name="checkboxPList" checked="checked" />'; 
	                    }
	                    
	                    /*
						var inpStr = '<input type="checkbox" class="' + class_dataset +'" id="'+ value['id'] +'" name="checkboxPList"/>'; 
	                    
	                    if(value['consolidated']==1){
	                    	var inpStr = '<input type="checkbox" class="' + class_dataset + '" id="'+ value['id'] +'" name="checkboxPList" checked="checked" />'; 
	                    }
	                    */
	                    $("<td />").html(inpStr).appendTo(row);
	                    //alert("prop id: " + value['id']);
	                    row.appendTo(table);
	                    cont = cont + 1;
					});					
					//alert('wp');
				}			
			});
			//$("#propertydiv").show();
		
			/* TAB 1 */
			$.get('RetrieveOntologies',function(responseJson){
				if(responseJson!=null){		
					
					$('#dataset').empty();
	                $('#dataset').append('<option value="">--Seleccione--</option>');
	                $('#dataset-Prop').append('<option value="">--Seleccione dataset--</option>');
	                console.log(responseJson);
					$.each(responseJson, function(key, value){
						//console.log(value);						
						$('#dataset-Prop').append("<option value='"  +  value['id'] + "'>" +  value['name'] + "</option>");
	                    $('#dataset').append("<option value='"  +  value['id'] + "'>" +  value['name'] + "</option>");
						$('#datasetMapping').append("<option value='"  +  value['id'] + "'>" +  value['name'] + "</option>");
					});					
				}			
			});
			
			$( "#datasetMapping" ).prop( "disabled", true );
			$( "#mappingInputProperty" ).prop( "disabled", true );
			
			$('#dataset').change(function(){
	            var idDat = $('#dataset').val();
	            console.log('idDat: '+idDat);
	            console.log(idDat);
	            
	            $.ajax({        
	                type: "GET",   
	                url: 'RetrieveDatasetClasses',
	                dataType : "JSON",
	                data: {
	                    idDataset: idDat,
	                },
	                success: function(responseJson){
	                	console.log(responseJson);
	                	if(responseJson!=null){		
				
	    					$('#class').empty();
	                        $('#class').append('<option value="">--Seleccione--</option>');
	                        
							$.each(responseJson, function(key, value){
								console.log('class:');
								console.log(value);
	                            $('#class').append("<option value='"  +  value['idClass'] + "'>" +  value['name'] + "</option>");
	
	    					});					
	    				}                       
	                },
	                error: function (e) {
	                    console.log('Ocurrio un error');
	                 	//console.log(e.responseText);
	                },
	
	            });
	        });
			
			$('#checkbox-mapping').change(function() {
		        if($(this).is(":checked")) {
					//enable select combobox
					//console.log('enable');
					$( "#datasetMapping" ).prop( "disabled", false );
					$( "#mappingInputProperty" ).prop( "disabled", false );
		        }
		        else{
		        	//disable select combobox
		        	//console.log('disable');
		        	$( "#datasetMapping" ).prop( "disabled", true );
		        	$( "#mappingInputProperty" ).prop( "disabled", true );
		        }    
		    })
		    
		
		/*modal propiedades*/
				
		// Get the modal
		//var modal = document.getElementById('myPropModal');
		
		// Get the button that opens the modal
		//var btn = document.getElementById("myBtn-prop");
		
		// Get the <span> element that closes the modal
		//var span = document.getElementsByClassName("close")[0];
		
		
// 		// When the user clicks the button, open the modal
// 		btn.onclick = function() {
// 		    modal.style.display = "block";
		    
// 		    //alert('x1');
// 		    $("#propertydiv").hide();
// 		    //$.get('/NavegadorLinkedData/RetrieveOntologies',function(responseJson){

// 		    $.get('Configuration/Property/Update',function(responseJson){
// 		    //$.get('UpdateProperty',function(responseJson){
		    	
// 		    	alert('UpdateProperty x2');
// 				if(responseJson!=null){					
// 					$("#propertytable").find("tr:gt(0)").remove();
// 					var table = $("#propertytable");
// 					//alert('no entro');
// 					alert("UpdateProperty gg");
// 					var cont = 1;
					
// 					alert(responseJson);
// 					console.log(responseJson);
// 					$.each(responseJson, function(key, value){
						
// 						var class_dataset = "dataset-" + value['dataset'];
// 	                    var class_class = "class-" + value['id_class'];
	                    
	                    
// 						var row = $('<tr class="'+ class_dataset + ' ' + class_class +'" />');
// 	                    $("<td />").text(value['uri']).appendTo(row);
// 	                    $("<td />").text(value['name']).appendTo(row);
	                    
	                    	                    
// 	                    var inpStr = '<input type="checkbox" class="' + class_dataset + ' ' + class_class + '" id="'+ value['id'] +'" name="checkboxPList"/>'; 
	                    
// 	                    if(value['consolidated']==1){
// 	                    	var inpStr = '<input type="checkbox" class="' + class_dataset + ' ' + class_class + '" id="'+ value['id'] +'" name="checkboxPList" checked="checked" />'; 
// 	                    }
	                    
// 	                    /*
// 						var inpStr = '<input type="checkbox" class="' + class_dataset +'" id="'+ value['id'] +'" name="checkboxPList"/>'; 
	                    
// 	                    if(value['consolidated']==1){
// 	                    	var inpStr = '<input type="checkbox" class="' + class_dataset + '" id="'+ value['id'] +'" name="checkboxPList" checked="checked" />'; 
// 	                    }
// 	                    */
// 	                    $("<td />").html(inpStr).appendTo(row);
// 	                    //alert("prop id: " + value['id']);
// 	                    row.appendTo(table);
// 	                    cont = cont + 1;
// 					});					
// 					//alert('wp');
// 				}			
// 			});
// 			$("#propertydiv").show();
			
// 		}
		
// 		// When the user clicks on <span> (x), close the modal
// 		span.onclick = function() {
// 		    modal.style.display = "none";
// 		}
		
// 		// When the user clicks anywhere outside of the modal, close it
// 		window.onclick = function(event) {
// 		    if (event.target == modal) {
// 		        modal.style.display = "none";
// 		    }
// 		}
		
		
		$("#dataset-Prop").change(function(){
			
			var rows = $('table.table-p tr');
			console.log('TODAS!');
			console.log(rows);
			var dataset = $("#dataset-Prop").val();
			
			alert("dataset: " + dataset);
					    
		    /* FILTRAR CLASES */
		    $.ajax({        
                type: "GET",   
                url: 'RetrieveDatasetClasses',
                dataType : "JSON",
                data: {
                    idDataset: dataset,
                },
                success: function(responseJson){
                	console.log(responseJson);
                	if(responseJson!=null){		
			
    					$('#class-Prop').empty();
                        $('#class-Prop').append('<option value="">--Seleccione--</option>');
                        
						$.each(responseJson, function(key, value){
							console.log('class:');
							console.log(value);
                            $('#class-Prop').append("<option value='"  +  value['idClass'] + "'>" +  value['name'] + "</option>");

    					});					
    				}                       
                },
                error: function (e) {
                    console.log('Ocurrio un error');
                 	//console.log(e.responseText);
                },

            });
		    
		    /*
		    var class_dataset = "dataset-" + dataset;                
		    var selected = rows.filter("."+class_dataset).show();
			console.log('SELECCIONADAS click dataset!');
			console.log(selected);
		    rows.not( selected ).hide();
		    */
		    
		    $(".table-head").show();
		});
		
		$('#class-Prop').change(function(){
			
			var rows = $('table.table-p tr');
			console.log('TODAS class!');
			console.log(rows);
			
			var clase = $("#class-Prop").val();
			var dataset = $("#dataset-Prop").val();
			
			alert("clase: " + clase);
			console.log("CLASE!!");
			console.log(clase);
			
			alert("dataset: " + dataset);
			console.log("dataset!!");
			console.log(dataset);
			
			var class_dataset = "dataset-" + dataset; 
			console.log("clase dataset: ");
			console.log(class_dataset);
			var class_clase = "class-" + clase; 
			console.log("clase clase: ");
			console.log(class_clase);
			

			var selected = rows.filter("."+class_dataset);			
			console.log('SELECCIONADAS 1!');
			console.log(selected);
			
		    var selected2 = selected.filter("."+class_clase).show();			
		    console.log('SELECCIONADAS 2!');
			console.log(selected2);
			
		    rows.not( selected2 ).hide();
		    $("#propertytable-head").show();
		    
		    $("#propertydiv").show();
		});
		
	});
	
		
	</script>
</body>
</html>