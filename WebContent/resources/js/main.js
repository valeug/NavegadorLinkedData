$(document).ready(function() {
    //console.log( "ready!" );
    
	$(".term").click(function(){
	    alert("The term was clicked.");
		var uri = $(this).text();
	    $("#searchbox").val(uri);
	});
});