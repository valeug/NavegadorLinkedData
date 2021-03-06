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
	<div class="container"> <!-- Only required for left/right tabs -->

	  	<div class="content">
	  		

	      		<form action="NewProperty" method="post">
					<div class="container" style="margin-top:100px;">
						
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
						    <label for="desc-prop">Ingresar descripci�n de la propiedad:</label>
						    <input type="text" class="form-control" id="descriptionInputProperty" name="descriptionInputProperty" placeholder="Descripcion">		
						</div>
						<div class="form-check">
			    			<label class="form-check-label">
			      			<input id = "checkbox-mapping" name="checkbox-mapping" class="form-check-input" type="checkbox"> Mapeo
						</div>
						<div class="form-group">
							<label for="dataset-sel">Seleccionar dataset objetivo</label>
							<select name="datasetMapping" id="datasetMapping" class="form-control">
							</select>
						</div>
						<!--
						<div class="form-group">
						    <label for="desc-prop">Ingresar valor a otro dataset</label>
						    <input type="text" class="form-control" id="mappingInputProperty" name="mappingInputProperty" placeholder="URI o c�digo">		
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
	
	
	

	<script type="text/javascript">
		
		$( document ).ready(function() {
			$.get('RetrieveOntologies',function(responseJson){
				if(responseJson!=null){		
					
					$('#dataset').empty();
                    $('#dataset').append('<option value="">--Seleccione--</option>');
                    console.log(responseJson);
					$.each(responseJson, function(key, value){
						//console.log(value);						
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
			
		});
	</script>
</body>
</html>