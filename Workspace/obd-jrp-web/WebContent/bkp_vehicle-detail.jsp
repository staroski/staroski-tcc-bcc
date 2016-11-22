<%@page import="br.com.staroski.obdjrp.web.ChartBuilder"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OBD-JRP</title>
<%
	ChartBuilder chart_builder = (ChartBuilder) request.getAttribute("chart_builder");
	out.println(chart_builder.createTagScript());
%>
</head>
<body>
	<div id="title" align="center">
		<h1><%=(String) request.getAttribute("vehicle_description")%></h1>
	</div>
	<%
		out.println(chart_builder.createTagDiv());
	%>
</body>
</html>
