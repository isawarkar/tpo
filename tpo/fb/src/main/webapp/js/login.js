// Login Form

$(function() {
    var button = $('#loginButton');
    var box = $('#loginBox');
    var form = $('#loginForm');
    button.removeAttr('href');
    button.mouseup(function(login) {
    	box.toggle();
        button.toggleClass('active');
    });
    form.mouseup(function() {
    	 return false;
    });
   
});

