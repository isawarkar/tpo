<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script data-ad-client="ca-pub-2495720134180716" async="true" src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>DailyRazor.com Customer's Website Coming Soon!!!</title>
</head>

<body>
	<%
		//String rUrl = request.getRequestURL().toString();
		String redirectURL = "exam/login/studentlogin.faces";
		/* if (rUrl != null && (rUrl.contains("bloodbank.") || rUrl.contains("blood."))) {
			redirectURL = "https://www.fresherbuddy.in/BloodBank/index.jsf";
		}
		 else if (rUrl != null && (rUrl.contains("uddanda."))) {
			redirectURL = "https://www.fresherbuddy.in/FB/Uddanda/index.jsf";
		} else {
			redirectURL = "https://www.fresherbuddy.in/FB/index.jsf";
		} */
		response.sendRedirect(redirectURL);
	%>
</body>
</html>
