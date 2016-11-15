<%@page import="java.util.List"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OBD-JRP</title>
</head>
<body>
	<p>Availiable Vehicles:</p>
	<%
		List<String[]> vehicles = (List<String[]>) request.getAttribute("vehicles");
	%>
	<div id="vehicle-list">
		<%
			for (String[] vehicle : vehicles) {
				out.println(String.format("<p><a href=\"get-data?vehicle=%s\">%s</a></p>", vehicle[0], vehicle[1]));
			}
		%>
	</div>
</body>
</html>
