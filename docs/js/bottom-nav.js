//hide all tabs
hideAllTabs();

//show only active tab
var y = document.getElementsByClassName("active");
show(y[0].id);

function show(tab){
	hideAllTabs();
	document.getElementById(tab).style.display = "block";
	document.getElementById(tab + "_button").className += " bottom-nav__action--active";
	scrollTo(0,0);
}

function hideAllTabs(){
	var x = document.getElementsByTagName("main");
	var y = document.getElementsByClassName("bottom-nav__action");
	for(i = 0; i < x.length; i++){
		x[i].style.display = "none";
		y[i].className = "bottom-nav__action";
	}
}