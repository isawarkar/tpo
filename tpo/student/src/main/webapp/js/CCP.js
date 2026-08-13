var x;
var t;


function refreshResult() {
	var refreshButton = document.getElementById("currentTestDashboardForm:refreshButton");
	refreshButton.click();
}



function keepAlive() {
	var qNo = document.getElementById("mainTest:quesionNumber").innerHTML;
	submitQuestion(qNo, 'set', null);
	checkTime();
}

var questionArray = new Array();

function submitUserForm(id) {
	var obj = document.getElementById(id);
	if (obj != null) {
		var closeButton =  document.getElementById('loginCloseButton');
		if (closeButton != null) {
			closeButton.click();
		}
		obj.click();
		pleaseWait(obj.value);
	}
}

function submitAdminForm(id) {
	var obj = document.getElementById(id);
	if (obj != null) {
		var closeButton =  document.getElementById('adminLoginCloseButton');
		if (closeButton != null) {
			closeButton.click();
		}
		obj.click();
		pleaseWait(obj.value);
	}
}
function setQuestionNo(qNumber) {
	checkTime();
	var qNo = document.getElementById("mainTest:quesionNumber");
	var qLink = document.getElementById("Q" + qNumber + "");
	if (!(qLink.style.cssText != '')) {
		qLink.style.cssText = yellowVar;
		var parentLi = qLink.parentNode;
		parentLi.setAttribute("class", "activeYellow");
	} else if (qLink.style.cssText == 'cursor: pointer;') {
		qLink.style.cssText = yellowVar;
		var parentLi = qLink.parentNode;
		parentLi.setAttribute("class", "activeYellow");
	} else if (qLink.style.cssText == 'cursor: hand;') {
		qLink.style.cssText = yellowVar;
		var parentLi = qLink.parentNode;
		parentLi.setAttribute("class", "activeYellow");
	}
	submitQuestion(qNumber, 'set', null);
	qNo.innerHTML = qNumber;
}

function setSelectedQ(qNumber) {
	var sectedVal = questionArray[qNumber];
	var optionA = document.getElementById("optionAR");
	var optionB = document.getElementById("optionBR");
	var optionC = document.getElementById("optionCR");
	var optionD = document.getElementById("optionDR");
	if (sectedVal != null && sectedVal != 'undifiend') {
		if (sectedVal.length == 1) {
			if (sectedVal == 'a') {
				optionA.checked = true;
			} else if (sectedVal == 'b') {
				optionB.checked = true;
			} else if (sectedVal == 'c') {
				optionC.checked = true;
			} else if (sectedVal == 'd') {
				optionD.checked = true;
			}

		} else {
			var values = sectedVal.split(',');
			optionA.checked = false;
			optionB.checked = false;
			optionC.checked = false;
			optionD.checked = false;
			for (i = 0; i < values.length; i++) {
				if (values[i] == 'a') {
					optionA.checked = true;
				} else if (values[i] == 'b') {
					optionB.checked = true;
				} else if (values[i] == 'c') {
					optionC.checked = true;
				} else if (values[i] == 'd') {
					optionD.checked = true;
				}
			}
		}
	} else {
		optionA.checked = false;
		optionB.checked = false;
		optionC.checked = false;
		optionD.checked = false;
	}
}
var yellowColor = 'yellow';
var greenColor = 'green';
var cyanColor = 'cyan';

var cyanVar = "color: black;cursor:hand;background: "
		+ cyanColor
		+ ";background: -webkit-gradient(linear, left top, left bottom, from("
		+ cyanColor
		+ "), to("
		+ cyanColor
		+ "));background: -moz-linear-gradient(top,  "
		+ cyanColor
		+ ",  "
		+ cyanColor
		+ ");filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='"
		+ cyanColor
		+ "', endColorstr='"
		+ cyanColor
		+ "', GradientType=0);-webkit-box-shadow: 0px 1px 2px rgba(0,0,0,.5);-moz-box-shadow: 0px 1px 2px rgba(0,0,0,.5);box-shadow: 0px 1px 2px rgba(0,0,0,.5);border-top: none;";

var yellowVar = "color: black;cursor:hand;background: "
		+ yellowColor
		+ ";background: -webkit-gradient(linear, left top, left bottom, from("
		+ yellowColor
		+ "), to("
		+ yellowColor
		+ "));background: -moz-linear-gradient(top,  "
		+ yellowColor
		+ ",  "
		+ yellowColor
		+ ");filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='"
		+ yellowColor
		+ "', endColorstr='"
		+ yellowColor
		+ "', GradientType=0);-webkit-box-shadow: 0px 1px 2px rgba(0,0,0,.5);-moz-box-shadow: 0px 1px 2px rgba(0,0,0,.5);box-shadow: 0px 1px 2px rgba(0,0,0,.5);border-top: none;";
var greenVar = "color: white;cursor:hand;background: "
		+ greenColor
		+ ";background: -webkit-gradient(linear, left top, left bottom, from("
		+ greenColor
		+ "), to("
		+ greenColor
		+ "));background: -moz-linear-gradient(top,  "
		+ greenColor
		+ ",  "
		+ greenColor
		+ ");filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='"
		+ greenColor
		+ "', endColorstr='"
		+ greenColor
		+ "', GradientType=0);-webkit-box-shadow: 0px 1px 2px rgba(0,0,0,.5);-moz-box-shadow: 0px 1px 2px rgba(0,0,0,.5);box-shadow: 0px 1px 2px rgba(0,0,0,.5);border-top: none;";

function tagQuestion() {
	checkTime();
	var qNo = document.getElementById("mainTest:quesionNumber");
	var qNo1 = qNo.innerHTML;
	var optionA = document.getElementById("optionAR");
	var optionB = document.getElementById("optionBR");
	var optionC = document.getElementById("optionCR");
	var optionD = document.getElementById("optionDR");

	var questionType = document.getElementById("mainTest:questionType");
	var answer = "z";
	if (questionType.value == 'Multiple') {
		if (optionA.checked == true) {
			if (answer == 'z') {
				answer = 'a';
			} else {
				answer = answer + ',a';
			}
		}
		if (optionB.checked == true) {
			if (answer == 'z') {
				answer = 'b';
			} else {
				answer = answer + ',b';
			}
		}
		if (optionC.checked == true) {
			if (answer == 'z') {
				answer = 'c';
			} else {
				answer = answer + ',c';
			}
		}
		if (optionD.checked == true) {
			if (answer == 'z') {
				answer = 'd';
			} else {
				answer = answer + ',d';
			}
		}
		questionArray[qNo.innerHTML] = answer;
	} else {
		if (optionA.checked == true) {
			answer = 'a';
			questionArray[qNo.innerHTML] = answer;
		} else if (optionB.checked == true) {
			answer = 'b';
			questionArray[qNo.innerHTML] = answer;
		} else if (optionC.checked == true) {
			answer = 'c';
			questionArray[qNo.innerHTML] = answer;
		} else if (optionD.checked == true) {
			answer = 'd';
			questionArray[qNo.innerHTML] = answer;
		}
	}
	var qLink = document.getElementById("Q" + qNo1 + "");
	qLink.style.cssText = cyanVar;
	var parentLi = qLink.parentNode;
	parentLi.setAttribute("class", "activeCyan");

	var number = ++qNo.innerHTML;
	var qLink1 = document.getElementById("Q" + number + "");
	if (qLink1 != null) {
		if (!(qLink1.style.cssText != '')) {
			qLink1.style.cssText = yellowVar;
			var parentLi = qLink1.parentNode;
			parentLi.setAttribute("class", "activeYellow");
		} else if (qLink1.style.cssText == 'cursor: pointer;') {
			qLink1.style.cssText = yellowVar;
			var parentLi = qLink1.parentNode;
			parentLi.setAttribute("class", "activeYellow");
		} else if (qLink1.style.cssText == 'cursor: hand;') {
			qLink1.style.cssText = yellowVar;
			var parentLi = qLink1.parentNode;
			parentLi.setAttribute("class", "activeYellow");
		}
	}
	submitQuestion(number, 'submit', answer);
	var numberOfquestions = document
			.getElementById("mainTest:numberOfquestions").value;
	if (numberOfquestions == qNo1) {
		qNo.innerHTML = --number;
	} else {
		qNo.innerHTML = number;
	}
}

function checkTime() {
	var time = document.getElementById("ms_timer").innerHTML;
	var array = time.split(":");
	var min = array[0];
	var sec = array[1];
	var timer = document.getElementById("ms_timer");
	var message = document.getElementById("mainTest:message");
	if (min < 5) {
		timer.className += " blinking";
		timer.style.borderColor = "#FFA500";
		// timer.style.color = "#FFA500";
		message.innerText = "Your have less than 5 minutes more.";
		message.style.color = "#FFA500";
		// timer.classList.add("blinking");
	}
	if (min < 1) {
		timer.className += " blinkingFast";
		// timer.style.color = "red";
		timer.style.borderColor = "red";
		message.innerText = "Your have less than 1 minute more.";
		message.style.color = "red";
		// timer.classList.add("blinking");
	}
}
function nextQuestion() {
	checkTime();
	var qNo = document.getElementById("mainTest:quesionNumber");
	var qNo1 = qNo.innerHTML;
	var optionA = document.getElementById("optionAR");
	var optionB = document.getElementById("optionBR");
	var optionC = document.getElementById("optionCR");
	var optionD = document.getElementById("optionDR");

	var questionType = document.getElementById("mainTest:questionType");
	var answer = "z";
	if (questionType.value == 'Multiple') {
		if (optionA.checked == true) {
			if (answer == 'z') {
				answer = 'a';
			} else {
				answer = answer + ',a';
			}
		}
		if (optionB.checked == true) {
			if (answer == 'z') {
				answer = 'b';
			} else {
				answer = answer + ',b';
			}
		}
		if (optionC.checked == true) {
			if (answer == 'z') {
				answer = 'c';
			} else {
				answer = answer + ',c';
			}
		}
		if (optionD.checked == true) {
			if (answer == 'z') {
				answer = 'd';
			} else {
				answer = answer + ',d';
			}
		}
		questionArray[qNo.innerHTML] = answer;
	} else {
		if (optionA.checked == true) {
			answer = 'a';
			questionArray[qNo.innerHTML] = answer;
		} else if (optionB.checked == true) {
			answer = 'b';
			questionArray[qNo.innerHTML] = answer;
		} else if (optionC.checked == true) {
			answer = 'c';
			questionArray[qNo.innerHTML] = answer;
		} else if (optionD.checked == true) {
			answer = 'd';
			questionArray[qNo.innerHTML] = answer;
		}
	}
	if (answer != "z") {
		var qLink = document.getElementById("Q" + qNo1 + "");
		qLink.style.cssText = greenVar;
		var parentLi = qLink.parentNode;
		parentLi.setAttribute("class", "activeGreen");
	}
	var number = ++qNo.innerHTML;
	var qLink1 = document.getElementById("Q" + number + "");
	if (qLink1 != null) {
		if (!(qLink1.style.cssText != '')) {
			qLink1.style.cssText = yellowVar;
			var parentLi = qLink1.parentNode;
			parentLi.setAttribute("class", "activeYellow");
		} else if (qLink1.style.cssText == 'cursor: pointer;') {
			qLink1.style.cssText = yellowVar;
			var parentLi = qLink1.parentNode;
			parentLi.setAttribute("class", "activeYellow");
		} else if (qLink1.style.cssText == 'cursor: hand;') {
			qLink1.style.cssText = yellowVar;
			var parentLi = qLink1.parentNode;
			parentLi.setAttribute("class", "activeYellow");
		}
	}
	submitQuestion(number, 'submit', answer);
	var numberOfquestions = document
			.getElementById("mainTest:numberOfquestions").value;
	if (numberOfquestions == qNo1) {
		qNo.innerHTML = --number;
	} else {
		qNo.innerHTML = number;
	}
}

function submitQuestion(qNumber, action, answer) {
	var url;
	var time = document.getElementById('ms_timer');
	document.getElementById('mainTest:submitActionButton').disabled = true;
	document.getElementById('mainTest:finishButtonAction').disabled = true;
	if (location.port == "") {
		url = location.protocol + "//" + location.hostname
				+ "/exam/servlet/SubmitQuestion?qNumber=" + escape(qNumber)
				+ "&action=" + escape(action) + "&answer=" + escape(answer)+"&time="
					+ escape(time.innerHTML);
	} else {
		url = location.protocol + "//" + location.host
				+ "/exam/servlet/SubmitQuestion?qNumber=" + escape(qNumber)
				+ "&action=" + escape(action) + "&answer=" + escape(answer)+"&time="
				+ escape(time.innerHTML);
	}
	loadingQuestion();
	ajaxCallWithoutForm(url, setSelectedQuestion, qNumber);
	questionsList();
}

function ajaxCallWithoutForm(pageurl, ajaxResponse, qNumber) {
	jQuery
			.ajax({
				url : pageurl,
				type : "POST",
				dataType : "json",
				success : function(data) {
					ajaxResponse(data);
					document.getElementById('mainTest:submitActionButton').disabled = false;
					document.getElementById('mainTest:finishButtonAction').disabled = false;
					hidePleaseWait();
					setSelectedQ(qNumber);
				},
				error : function(jqXHR, textStatus, errorThrown) {
					if (jqXHR.status != null && jqXHR.status == 500) {
						alert(jqXHR.responseText);
					} else if (jqXHR.status == 401) {
						alert(jqXHR.responseText);
						document
								.getElementById("mainTest:sessionExpiredAction")
								.click();
						;
					} else if (jqXHR.status == 400) {
						$("#lastQuestion").dialog("open");
					} else {
						alert(errorThrown);
					}
					document.getElementById('mainTest:submitActionButton').disabled = false;
					document.getElementById('mainTest:finishButtonAction').disabled = false;
					hidePleaseWait();
				}
			});

}

function setSelectedQuestion(data) {
	if (data != null) {
		var questionType = document.getElementById("mainTest:questionType");
		var question = document.getElementById("mainTest:question");
		var a = document.getElementById("mainTest:optionAText");
		var b = document.getElementById("mainTest:optionBText");
		var c = document.getElementById("mainTest:optionCText");
		var d = document.getElementById("mainTest:optionDText");
		$.each(data, function(index, value) {
			question.innerHTML = this.question;
			a.innerHTML = this.optiona;
			b.innerHTML = this.optionb;
			c.innerHTML = this.optionc;
			d.innerHTML = this.optiond;
			questionType.value = this.questionType;
			var img = document.getElementById('imageText');
			var img1 = document.getElementById('imageText1');
			if (this.isImage == 1) {
				img.style.display = "block";
				img.src = 'data:image/jpeg;base64,' + this.image;
				img.height = "400";
				img.width = "700";
				img1.src = 'data:image/jpeg;base64,' + this.image;
			} else {
				img.style.display = "none";
				img.src = '';
				img.height = "0";
				img.width = "0";
			}
			// document.body.appendChild(img);

			var optionA = document.getElementById("optionAR");
			var optionB = document.getElementById("optionBR");
			var optionC = document.getElementById("optionCR");
			var optionD = document.getElementById("optionDR");
			if (questionType.value == 'Multiple') {
				optionA.type = "checkbox";
				optionB.type = "checkbox";
				optionC.type = "checkbox";
				optionD.type = "checkbox";
			} else {
				optionA.type = "radio";
				optionB.type = "radio";
				optionC.type = "radio";
				optionD.type = "radio";
			}

		});
	}
}

function setQuestion(responseXML) {
	var questions = responseXML.getElementsByTagName("questions")[0];
	if (questions != null) {
		var question = questions.childNodes[0];
		var optionA = questions.childNodes[1];
		var optionB = questions.childNodes[2];
		var optionC = questions.childNodes[3];
		var optionD = questions.childNodes[4];
		document.getElementById("mainTest:question").innerHTML = question.childNodes[0].nodeValue;
		document.getElementById("mainTest:optionAText").innerHTML = optionA.childNodes[0].nodeValue;
		document.getElementById("mainTest:optionBText").innerHTML = optionB.childNodes[0].nodeValue;
		document.getElementById("mainTest:optionCText").innerHTML = optionC.childNodes[0].nodeValue;
		document.getElementById("mainTest:optionDText").innerHTML = optionD.childNodes[0].nodeValue;
	}
}

function overExam() {
	finish = document.getElementById("mainTest:finishHiddenButtonAction");
	if (finish != null) {
		submitYourExam();
	} else {
		alert(document.getElementById("mainTest:hiddenMessgage2").innerHTML);
	}

}

function setTimeTaken() {
	checkTime();
	var tagged = document.getElementById("mainTest:tagged");
	if (tagged.innerHTML > 0) {
		$("#taggedQuestions").dialog("open");
		return false;
	} else {
		submitYourExamNew();
	}
}

function submitYourExamNew() {
	$("#taggedQuestions").dialog("close");
	var time = document.getElementById('ms_timer');
	var url;
	if (location.port == "") {
		url = location.protocol + "//" + location.hostname
				+ "/exam/servlet/SubmitQuestion?time="
				+ escape(time.innerHTML);
	} else {
		url = location.protocol + "//" + location.host
				+ "/exam/servlet/SubmitQuestion?time="
				+ escape(time.innerHTML);
	}
	var req = initRequest(url);
	req.onreadystatechange = function() {
		if (req.readyState === 4 && req.status === 200) {
			finish = document
					.getElementById("mainTest:finishHiddenButtonAction");
			if (finish != null) {
				finish.click();
				var msg = document
						.getElementById("mainTest:Please_wait_for_result").innerHTML;
				pleaseWait(msg);
			}
		}
		;
	}
	req.open("POST", url, true);
	req.send(null);

}

function submitYourExam() {
	var time = document.getElementById('ms_timer');
	var url;
	if (location.port == "") {
		url = location.protocol + "//" + location.hostname
				+ "/exam/servlet/SubmitQuestion?time="
				+ escape(time.innerHTML);
	} else {
		url = location.protocol + "//" + location.host
				+ "/exam/servlet/SubmitQuestion?time="
				+ escape(time.innerHTML);
	}
	var req = initRequest(url);
	req.onreadystatechange = function() {
		if (req.readyState === 4 && req.status === 200) {
			finish = document.getElementById("mainTest:finishHiddenButtonAction");
			var msg = document.getElementById("mainTest:hiddenMessgage1").innerHTML;
			//alert(msg);
			finish.click();
			pleaseWait(document.getElementById("mainTest:Please_wait_for_result").innerHTML);
		}
		;
	}
	req.open("POST", url, true);
	req.send(null);

}

// Please Wait Start
function pleaseWait(msg) {
	document.getElementById("LoaderX").style.display = "block";
	document.getElementById("LoaderContainer").innerHTML = msg;
	show('LoaderX');
}
function loadingQuestion() {
	document.getElementById("LoaderX").style.display = "block";
	show('LoaderX');
}
function pleaseWaitLoading() {
	document.getElementById("LoaderX").style.display = "block";
	show('LoaderX');
}
function hidePleaseWait() {
	document.getElementById("LoaderContainer").innerHTML = "";
	document.getElementById("LoaderX").style.display = "none";
	close('LoaderX');
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

function validateEmail(strEmail) {
	var at = "@";
	var dot = ".";
	var lat = strEmail.indexOf(at);
	var lstr = strEmail.length;
	ldot = strEmail.indexOf(dot);
	if (strEmail.indexOf(at) == -1) {
		alert("Invalid E-mail ID")
		return false

	}
	if (strEmail.indexOf(at) == -1 || strEmail.indexOf(at) == 0
			|| strEmail.indexOf(at) == lstr) {
		alert("Invalid E-mail ID")
		return false
	}
	if (strEmail.indexOf(dot) == -1 || strEmail.indexOf(dot) == 0
			|| strEmail.indexOf(dot) == lstr) {
		alert("Invalid E-mail ID")
		return false
	}
	if (strEmail.indexOf(at, (lat + 1)) != -1) {
		alert("Invalid E-mail ID")
		return false
	}
	if (strEmail.substring(lat - 1, lat) == dot
			|| strEmail.substring(lat + 1, lat + 2) == dot) {
		alert("Invalid E-mail ID")
		return false
	}
	if (strEmail.indexOf(dot, (lat + 2)) == -1) {
		alert("Invalid E-mail ID")
		return false
	}
	if (strEmail.indexOf(" ") != -1) {
		alert("Invalid E-mail ID")
		return false
	}
	return true
}

function initRequest() {
	if (window.XMLHttpRequest) {
		return new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		isIE = true;
		return new ActiveXObject("Microsoft.XMLHTTP");
	}
}

function findElement(elementId) {
	if (document.getElementById(elementId))
		return elementId;
	for (var i = 0; i < document.forms.length; i++) {
		if (document.getElementById(document.forms[i].id + ':' + elementId)) {
			return document.forms[i].id + ':' + elementId;
		}
	}
	return null;
}
function uploadImage() {
	var imageFile = document.getElementById('imageFile');
	if (imageFile.value == null || imageFile.value == '') {
		alert('Please select image file');
		return false;
	} else {
		document.getElementById('loadMessage').style.display = 'block';
		document.getElementById('loadMessage').innerHTML = 'Please wait uploading';
		return true;
	}
}

function checkIfPopWindowBlocked() {
	var windowName = 'userConsole';
	var popUp = window.open('https://www.google.com', windowName,
			'width=1, height=1, left=24, top=24, scrollbars, resizable');
	if (popUp == null || typeof (popUp) == 'undefined') {
		alert('Please disable your pop-up blocker.');
		document.getElementById('onlineTest:StartTest').disabled = true;
	} else {
		document.getElementById('onlineTest:StartTest').disabled = false;
		popUp.close();
	}
}

function checkChapta() {
	var EnterImageText = document.getElementById("onlineTest:EnterImageText").value;
	if (EnterImageText != "") {
		var image = document.getElementById("onlineTest:ImageText").value;
		if (EnterImageText != image) {
			alert(document.getElementById("onlineTest:ChaptaImageText").innerHTML);

		}
	}
}

function openChat(name, pass, fullNameOFUser) {
	require([ 'converse' ], function(converse) {
		(function() {
			/*
			 * XXX: This function initializes jquery.easing for the
			 * https://conversejs.org website. This code is only useful in the
			 * context of the converse.js website and converse.js itself is NOT
			 * dependent on it.
			 */
			var $ = converse.env.jQuery;
			$.extend($.easing, {
				easeInOutExpo : function(x, t, b, c, d) {
					if (t == 0)
						return b;
					if (t == d)
						return b + c;
					if ((t /= d / 2) < 1)
						return c / 2 * Math.pow(2, 10 * (t - 1)) + b;
					return c / 2 * (-Math.pow(2, -10 * --t) + 2) + b;
				},
			});

			$(window).scroll(function() {
				if ($(".navbar").offset().top > 50) {
					$(".navbar-fixed-top").addClass("top-nav-collapse");
				} else {
					$(".navbar-fixed-top").removeClass("top-nav-collapse");
				}
			});
			// jQuery for page scrolling feature - requires jQuery Easing plugin
			$('.page-scroll a').bind('click', function(event) {
				var $anchor = $(this);
				$('html, body').stop().animate({
					scrollTop : $($anchor.attr('href')).offset().top
				}, 700, 'easeInOutExpo');
				event.preventDefault();
			});
		})();
		converse.initialize({
			bosh_service_url : 'https://conversejs.org/http-bind/', // Please
			// use this
			// connection
			// manager
			// only for
			// testing
			// purposes
			auto_login : true,
			authentication : 'login',
			jid : name,
			password : pass,
			keepalive : true,
			message_carbons : true,
			play_sounds : true,
			roster_groups : true,
			/* show_controlbox_by_default: true, */
			default_domain : "conversejs.org",
			domain_placeholder : "conversejs.org",
			animate : true,
			fullname : fullNameOFUser
		/* hide_muc_server:true */
		/* hide_offline_users:true */

		});
	});
}

function startExamFirstQuestion() {
	setInterval(keepAlive, 290000);
	var questionType = document.getElementById("mainTest:questionType");
	var optionA = document.getElementById("optionAR");
	var optionB = document.getElementById("optionBR");
	var optionC = document.getElementById("optionCR");
	var optionD = document.getElementById("optionDR");
	if (questionType.value == 'Multiple') {
		optionA.type = "checkbox";
		optionB.type = "checkbox";
		optionC.type = "checkbox";
		optionD.type = "checkbox";
	} else {
		optionA.type = "radio";
		optionB.type = "radio";
		optionC.type = "radio";
		optionD.type = "radio";
	}
}

function questionsList() {
	var visitedQ = 0;
	var submittedQ = 0;
	var notVisitedQ = 0;
	var taggedQ = 0;
	var lis = document.getElementById("content").getElementsByTagName("li");
	for (var i = 0; i < lis.length; i++) {
		var element = lis[i];
		if (element.getAttribute('class') == 'activeBlue') {
			notVisitedQ++;
		}
		if (element.getAttribute('class') == 'activeYellow') {
			visitedQ++;
		}
		if (element.getAttribute('class') == 'activeGreen') {
			submittedQ++;
		}
		if (element.getAttribute('class') == 'activeCyan') {
			taggedQ++;
		}
	}
	var visited = document.getElementById("mainTest:visited");
	var submitted = document.getElementById("mainTest:submitted");
	var notVisited = document.getElementById("mainTest:notVisited");
	var tagged = document.getElementById("mainTest:tagged");
	var tagged1 = document.getElementById("mainTest:tagged1");
	var tagged2 = document.getElementById("tagged2");
	visited.innerHTML = visitedQ;
	submitted.innerHTML = submittedQ;
	notVisited.innerHTML = notVisitedQ;
	tagged.innerHTML = taggedQ;
	tagged1.innerHTML = taggedQ;
	tagged2.innerHTML = taggedQ;
		
}

function showPassword(formId, id) {
	var obj;
	if (formId != null) {
		obj = document.getElementById(formId + ":" + id);
	} else {
		obj = document.getElementById(id);
	}
	if (obj != null && obj.type === "password") {
		obj.type = "text";
	} else {
		obj.type = "password";
	}
}

function blinker() {
	$('.blinking').fadeOut(1000);
	$('.blinking').fadeIn(1000);
}
setInterval(blinker, 1000);

function blinkerFast() {
	$('.blinkingFast').fadeOut(500);
	$('.blinkingFast').fadeIn(500);
}
setInterval(blinkerFast, 500);

function showNotice(id) {
	var obj = document.getElementById(id);
	if (obj != null) {
		obj.click();
	}
}
