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
	/*
	$(".term").click(function(){
	    alert("The term was clicked.");
		var parent = $(this).parent();
		console.log(parent);
		alert(parent.text());
		var sib = parent.find( ".linked-t-uris" );
	    $("#searchbox").val(sib.text());
	    $("#search-button-results").click();
	});
	*/
	//
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
	
	$(".ref-value").click(function(){
	    alert("The term was clicked.");
		var parent = $(this).parent();		
		//var parentId = parent.attr("id");
		console.log("parent!!")
		console.log(parent);
		var input = "";
		if(parent.hasClass("prop-label-1") || parent.hasClass( "prop-label-4")){
			var sib = parent.find( ".ref-value" );
			input = sib.text();
		}
		if(parent.hasClass("prop-label-2")){
			var label_parent = $(this).parents("div .prop-label-div");
			console.log("label parent");
			console.log(label_parent);
			console.log("prop label 3");
			console.log(label_parent.find('.prop-label-3'));
			input = label_parent.find('.prop-label-3').text();
			console.log("input");
			console.log(input);
		}
		if(parent.hasClass("prop-label-5")){
			var label_parent = $(this).parents("div .prop-label-div");
			input = label_parent.find('.prop-label-6').text();
		}
		console.log("input: " + input);
	    $("#searchbox").val(input);
	    
	    //property uri (hidden input)
	    var div_parent = $(this).parents("div .prop-group-elem");
	    console.log("div_parent");
		console.log(div_parent);
		var prop_uri = div_parent.find('.prop-group-uri');
		console.log("prop_uri");
		console.log(prop_uri.text());
		$("#property-uri-input").val(prop_uri.text());		
		console.log(prop_uri.text());
	    $("#search-button-results").click();
	});
	
	
	$(".label-detail").click(function(){
		var div_parent = $(this).parents("div .see-detail-div");
	    console.log("div_parent");
		console.log(div_parent);
		var prop_uri = div_parent.find('.concept-detail-uri');
		console.log("prop_uri");
		console.log(prop_uri.text());
		$("#searchbox").val(prop_uri.text());		
		//console.log(prop_uri.text());
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