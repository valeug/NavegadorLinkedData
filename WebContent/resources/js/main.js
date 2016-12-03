$(document).ready(function() {
    //console.log( "ready!" );
    
	/*
	//Ajax (para obtener la lista de ontologias de la BD y mostrarlas en 
	// el modal de la pagina principal)
	$("#ontologydiv").hide();
	$("#myBtn").click(function(){
		$.get('/RetrieveOntologies',function(responseJson){
			if(responseJson!=null){
				$("#ontologytable").find("tr:gt(0)").remove();
				var table = $("#ontologytable");
				alert(value['no entro']);
				$.each(responseJson, function(key, value){
					var rowNew = $("<tr><td></td><td></td></tr>");
					alert(value['name']);
					rowNew.children().eq(0).text(value['name']);
					rowNew.children().eq(1).text(value['description']);
					rowNew.appendTo(table);
				});				
			}			
		});
		$("#ontologydiv").show();
	});
	*/
	
	
	//Para iniciar navegacion a terminos parecidos
	// al dar click, se agrega el termino a la barra de busqueda 
	$(".term").click(function(){
	    alert("The term was clicked.");
		var parent = $(this).parent();
		console.log(parent);
		alert(parent.text());
		var sib = parent.find( ".linked-t-uris" );
	    $("#searchbox").val(sib.text());
	    $("#searchbox").click();
	});
	
	
	
	
	//save selected ontologies
	$("#saveOntoBtn").click(function(){
		var arr = [];
		$("input:checkbox[name=checkboxList]:checked").each(function(){
			var element = $(this);
			arr.push(element.attr('id'));
			//alert(element.attr('id'));
			//alert('asd');
		});
		//alert('ontoBtn');
		//var arr=[1,2,3,4];
		$.ajax({
			url:'RetrieveOntologies',
			type:'POST',
			dataType:'json',
			data:{arr:arr},
	        success : function(data){
	            alert('success post');
	        }
	    });
	});
	
	
	//save selected ontologies
	$("#updatePropBtn").click(function(){
		var arr = [];
		$("input:checkbox[name=new_checkboxList]:checked").each(function(){
			var element = $(this);	
			var itemList = element.closest('tr');
			var uriProp = ""+itemList.children('td')[0].innerHTML;
			var list = document.getElementById('prop-list');
			
			var elements = list.children;
			//console.log(elements);
			$.each(elements, function(key, value){
				var uriPshow = ""+value.children[0].innerHTML; //uri de propiedad en la descripcion
				//console.log(uriPshow);
				if(uriProp == uriPshow){
					//console.log('lo agrego');
					value.classList.remove("not-show-default");
					value.classList.add("show-default");
					value.style.display = "block";
				}
			});
		});
	});
	
			
});