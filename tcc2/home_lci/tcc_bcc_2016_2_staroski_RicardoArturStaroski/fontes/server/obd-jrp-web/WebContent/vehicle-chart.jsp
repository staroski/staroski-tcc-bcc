<%@page import="br.com.staroski.obdjrp.web.ChartBuilder"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%
	final String vehicle = (String) request.getAttribute("vehicle");
	final String pid = (String) request.getAttribute("pid");
	final ChartBuilder chart_builder = (ChartBuilder) request.getAttribute("chart_builder");
%>
<!DOCTYPE HTML PUBLIC>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>OBD-JRP</title>
<%=chart_builder.createTagScript()%>
</head>
<body>
	<div id="title" align="center">
		<h1>Vehicle</h1>
		<h2><%=vehicle%></h2>
	</div>
	<form action="exec">
		<input type="hidden" name="cmd" value="ReadData" />
		<input type="hidden" name="vehicle" value="<%=vehicle%>" />
		<input type="hidden" name="history" value="yes" />
		<input type="hidden" name="pid" value="<%=pid%>" />
		<div align="center">
			<input type="submit" value="View History" />
		</div>
	</form>
	<div align="center">
		<%=chart_builder.createTagDiv()%>
	</div>
</body>
</html>
