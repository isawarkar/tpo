/* Side Nav function
-------------------------------------------*/

$('.leftmenutrigger').on('click', function (e) {
	$('.side-nav').toggleClass("open");
	$('#wrapper').toggleClass("open");
	e.preventDefault();
});