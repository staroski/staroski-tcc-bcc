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
	<div id="title" align="center">
		<h1>Vehicles</h1>
	</div>
	<%
		List<String> vehicles = (List<String>) request.getAttribute("vehicles");
	%>
	<div id="vehicle-list" align="center">
		<%
			for (String vehicle : vehicles) {
				out.println(String.format("<p><a href=\"exec?cmd=ReadData&vehicle=%s\">%s</a></p>", vehicle, vehicle));
			}
		%>
	</div>
</body>
</html>
