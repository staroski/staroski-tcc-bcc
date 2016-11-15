<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="br.com.staroski.obdjrp.web.HtmlChartBuilder"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OBD-JRP</title>
<%
	final String vehicle_description = (String) request.getAttribute("vehicle_description");
	final Date scan_time = new Date((Long) request.getAttribute("scan_time"));
	final String formatted_scan_time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS").format(scan_time);
	final HtmlChartBuilder chart_builder = (HtmlChartBuilder) request.getAttribute("chart_builder");
%>
<%=chart_builder.createTagScriptGoogle()%>
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
		<%=chart_builder.createTagDiv_chart()%>
	</div>
</body>
</html>
