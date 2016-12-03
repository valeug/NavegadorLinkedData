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
	holi ;)
	
	<form action="ConfigurationServlet" method="post">
		<div class="container">
			
			<div class="form-group">
				<label for="dataset-sel">Seleccionar dataset</label>
				<select name="resultado" id="dataset" class="form-control">
				</select>
			</div>
			<div class="form-group">
				<label for="class-sel">Seleccionar clase</label>
				<select name="class" id="class" class="form-control">
				</select>
			</div>	
			<div class="form-group">
			    <label for="uri-prop">Ingresar URI de la propiedad:</label>
			    <input type="text" class="form-control" id="uriInputProperty" placeholder="URI">		
			</div>
			<div class="form-group">
			    <label for="name-prop">Ingresar nombre de la propiedad:</label>
			    <input type="text" class="form-control" id="nameInputProperty" placeholder="Nombre">		
			</div>
			<div class="form-group">
			    <label for="desc-prop">Ingresar descripción de la propiedad:</label>
			    <input type="text" class="form-control" id="descriptionInputProperty" placeholder="Descripcion">		
			</div>
			
			<button type="submit" class="btn btn-primary">Aceptar</button>
			<a href="/NavegadorLinkedData" class="btn btn-primary">Salir</a>
			
	</form>

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
					});					
				}			
			});
			
			
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
                                $('#class').append("<option value='"  +  value['id_class'] + "'>" +  value['name'] + "</option>");

        					});					
        				}                       
                    },
                    error: function (e) {
                        console.log('Ocurrio un error');
                     	//console.log(e.responseText);
                    },

                });
            });
			
		});
	</script>
</body>
</html>