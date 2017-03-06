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
	    $("#search-button-results").click();
	});
	
	
	$(".ref-value").click(function(){
	    alert("The term was clicked.");
		var parent = $(this).parent();
		console.log("parent");
		console.log(parent);
		//alert(parent);
		//alert(parent.text());
		/*
		var sib = parent.find( ".ref-value" );
		console.log("parent: ");
		console.log(parent);
		console.log("sib: ");
		console.log(sib);
		console.log("sib id: "); 
		console.log(sib.getAttribute("id"));
		*/
		var parentId = parent.attr("id");;
		console.log(parentId);
		var input = "";
		if(parentId == "prop-label-1" || parentId == "prop-label-4"){
			var sib = parent.find( ".ref-value" );
			input = sib.text();
		}
		if(parentId == "prop-label-2"){
			//console.log("pl3");
			//console.log(document.getElementById('prop-label-3'));
			input = document.getElementById('prop-label-3').innerHTML; 
		}
		if(parentId == "prop-label-5"){
			//console.log("pl6");
			//console.log(document.getElementById('prop-label-6'));
			input = document.getElementById('prop-label-6').innerHTML;
		}
		//alert(sib);
		//console.log("sib");
		//console.log(sib);
		//console.log(sib.text());
		console.log("input: " + input);
	    $("#searchbox").val(input);
	    $("#search-button-results").click();
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
	
	// actualizar propiedades
	$("#savePropBtn").click(function(){
		var arr = [];
		//alert('post !!!!')
		$("input:checkbox[name=checkboxPList]:checked").each(function(){
			var element = $(this);
			console.log($(this));
			arr.push(element.attr('id'));
			arr.push(element.attr('class'));
			//alert(element.attr('class'));
			//alert('asd');
		});
		//alert('ontoBtn');
		console.log("arreglo");
		console.log(arr);
		//var arr=[1,2,3,4];
		$.ajax({
			url:'UpdateProperty',
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