<%@page import="br.com.staroski.obdjrp.web.HtmlChartBuilder"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OBD-JRP</title>
<%
	HtmlChartBuilder chart_builder = (HtmlChartBuilder) request.getAttribute("chart_builder");
	out.println(chart_builder.createTagScriptGoogle());
%>
</head>
<body>
	<%
		out.println(chart_builder.createTagDiv_chart());
	%>
</body>
</html>
