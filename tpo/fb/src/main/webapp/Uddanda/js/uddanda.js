function sendEmail(currenObj) {
      	var url;
		var name = document.getElementById("name");
		if(name.value == ''){
			name.style.background = 'red';
			alert('Please enter name');
			return;
		}else{
			name.style.background = 'gray';
		}
		var email = document.getElementById("email");
		if(email.value == ''){
			email.style.background = 'red';
			alert('Please enter email');
			return;
		}else{
			email.style.background = 'gray';
		}
		var message = document.getElementById("message");
		if(message.value == ''){
			message.style.background = 'red';
			alert('Please enter message');
			return;
		}else{
			message.style.background = 'gray';
		}
		currenObj.innerHTML = "Please wait....";
		
		if(location.port == ""){
				url = location.protocol + "//"+location.hostname+"/UT/servlet/SendEmail?name="+escape(name.value)+"&email="+escape(email.value)+"&message="+escape(message.value);
			} else {
				url = location.protocol + "//"+location.host+"/UT/servlet/SendEmail?name="+escape(name.value)+"&email="+escape(email.value)+"&message="+escape(message.value);
			}
		var req = initRequest(url);
        req.onreadystatechange = function() {
            if (req.readyState == 4) {
                if (req.status == 200) {
                    setEmailMessage(req.responseXML);
                    currenObj.innerHTML = "Send Your Message";
                } 
            }
        };
        req.open("POST", url, true);
        req.send(null);
        currenObj.innerHTML = "Send Your Message";
}

function setEmailMessage(responseXML) {
	   var emails = responseXML.getElementsByTagName("messages")[0];
	  	for (loop = 0; loop < emails.childNodes.length; loop++) {
		    var email = emails.childNodes[loop];
		    if( email.childNodes[0].nodeValue == 'Invalid E-mail address'){
		    	var emailOBJ = document.getElementById("email");
		    	emailOBJ.style.background = 'red';
		    	alert("Invalid E-mail address");
		    }else {
		    	var emailOBJ = document.getElementById("email");
		    	emailOBJ.style.background = 'gray';
				alert(email.childNodes[0].nodeValue);
		    }
	  }
	}

function initRequest() {
    if (window.XMLHttpRequest) {
        return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        isIE = true;
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
}

//Please Wait Start
function pleaseWait() {
	var url;
	if(location.port == ""){
			url = location.protocol + "//"+location.hostname+"/UT/";
		} else {
			url = location.protocol + "//"+location.host+"/UT/";
		}
	url = url + "images/pleasewait.gif";
	document.getElementById("LoaderX").style.display = "block";
	document.getElementById("LoaderContainer").innerHTML = "<img src="+url+">";
	show('LoaderX');
}
function hidePleaseWait() {
	document.getElementById("LoaderContainer").innerHTML = "";
	document.getElementById("LoaderX").style.display = "none";
}
function show(id) {
	document.getElementById(id).style.display = 'block';
	document.getElementById('overlay').className = 'overlayBlockDiv';
	if (window.innerHeight && window.scrollMaxY) {// Firefox
		yWithScroll = window.innerHeight + window.scrollMaxY;
		xWithScroll = window.innerWidth + window.scrollMaxX;
	} else if (document.body.scrollHeight > document.body.offsetHeight) { 
		yWithScroll = document.body.scrollHeight;
		xWithScroll = document.body.scrollWidth;
	} else { 
		yWithScroll = document.body.offsetHeight;
		xWithScroll = document.body.offsetWidth;
	}
	document.getElementById('overlay').style.height = yWithScroll + "px";
}

function close(id) {
	if (document.getElementById(id)) {
		document.getElementById(id).style.display = 'none';
		document.getElementById('overlay').className = '';
		document.getElementById('overlay').style.height = "0px";
	}
}
// End Please wait
