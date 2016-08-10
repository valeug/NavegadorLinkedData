function callServletWithAjax(methodtype){
	
	var xmlhttp;
	if(window.XMLHttpRequest){
		xmlhttp = new XMLHttpRequest();
	}
	else{
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	xmlhttp.onreadystatechange = function(){
		if(xmlhttp.readyState == 4 && xmlhttp.status ==200){
			//document.getElementById("mydiv").innerHTML = "baia baia";
			var result = xmlhttp.responseText; //this is what you want
		}
	};
	
	var params = "uri=zelda";
	if(methodtype == 'POST'){
		xmlhttp.open(methodtype,"/NavegadorLinkedData/AppServlet",true);
		xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xmlhttp.send(params);		
	}
	else{
		alert('algo paso :/')
	}
}

function myfunction(obj) {
	alert('holi');
	
	callServletWithAjax('POST');
}

//best way
//$('a').click(function(){ myfunction(this); return false; });
