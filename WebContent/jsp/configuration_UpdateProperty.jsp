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

	<div class="container">
		<form action="UpdateProperty" method="post">
			<table class="table" cellspacing="0" id="propertytable">
				<tr>
					<th scope="col">URI</th>
					<th scope="col">Seleccionar</th>
				</tr>
				<c:forEach items="${propiedades}" var="i">
					<tr>
						<td><c:out value="${i.uri}" /></td>
						<td>
							<c:if test="${i.consolidated == 1}">
								<input type="checkbox" checked="checked" />
							</c:if>
							<c:if test="${i.consolidated == 0}">
								<input type="checkbox" />
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</table>
		</form>
	
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