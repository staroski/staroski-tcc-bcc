<%@page import="br.com.staroski.obdjrp.web.HistoryTableModel"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="styles.css">
<title>OBD-JRP</title>
</head>
<body>
<%  final String vehicle = (String) request.getAttribute("vehicle");
	final String pid = (String) request.getAttribute("pid");
	final HistoryTableModel model = (HistoryTableModel) request.getAttribute("history_table_model");
	final int rows = model.getRowCount();
	final int columns = model.getColumnCount();
	final int lastColumn = columns - 1; %>
	<div id="title" align="center">
		<h1>Vehicle</h1>
		<h2><%=vehicle%></h2>
	</div>
	<div id="title" align="center">
		<h2>PID <%=pid%> History</h2>
	</div>
	<div id="scan-data" align="center">
		<div class="divTable">
			<div class="divTableBody">
				<div class="divTableRow">
					<% for (int j = 0; j < columns; j++) { %>
					<div class="divTableCell" align="center"><%=model.getColumnName(j)%></div>
					<% } %>
				</div>
				<% for (int i = 0; i < rows; i++) { %>
					<div class="divTableRow">
					<% for (int j = 0; j < columns; j++) {
							String alignment = j == lastColumn ? "right" : "left";
							String value = (String) model.getValueAt(i, j); %>
					<div class="divTableCell" align=<%=alignment%>><%=value%></div>
					<% } %>
				</div>
				<% } %>
			</div>
		</div>
	</div>
</body>
</html>
