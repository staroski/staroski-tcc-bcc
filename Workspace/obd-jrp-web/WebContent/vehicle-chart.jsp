<%@page import="br.com.staroski.obdjrp.web.JsonChartBuilder"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%
	final String vehicle_description = (String) request.getAttribute("vehicle_description");
	final Date scan_time = new Date((Long) request.getAttribute("scan_time"));
	final String formatted_scan_time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS").format(scan_time);
	final JsonChartBuilder chart_builder = (JsonChartBuilder) request.getAttribute("chart_builder");
%>
<!DOCTYPE HTML PUBLIC>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OBD-JRP</title>
<%
	out.println(chart_builder.createTagScript());
%>
</head>
<body>
	<div id="title" align="center">
		<h1><%=vehicle_description%></h1>
	</div>
	<div align="center">
		<p>
			Scanned in
			<%=formatted_scan_time%></p>
	</div>
	<div align="center">
		<%
			out.println(chart_builder.createTagDiv());
		%>
	</div>
</body>
</html>
