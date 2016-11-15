<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="br.com.staroski.obdjrp.web.ScanTableModel"%>
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
	<%
		final String vehicle_id = (String) request.getAttribute("vehicle");
		final String vehicle_description = (String) request.getAttribute("vehicle_description");
		final ScanTableModel model = (ScanTableModel) request.getAttribute("scan_table_model");
		final int rows = model.getRowCount();
		final int columns = model.getColumnCount();
		final int lastColumn = columns - 1;
		final Date scan_time = new Date((Long) request.getAttribute("scan_time"));
		final String formatted_scan_time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS").format(scan_time);
	%>
	<div id="title" align="center">
		<h1><%=vehicle_description%></h1>
	</div>
	<div align="center">
		<p>
			Scanned in
			<%=formatted_scan_time%></p>
	</div>
	<div id="scan-data" align="center">
		<div class="divTable">
			<div class="divTableBody">
				<div class="divTableRow">
					<%
						for (int j = 0; j < columns; j++) {
					%>
					<div class="divTableCell" align="center"><%=model.getColumnName(j)%></div>
					<%
						}
					%>
				</div>
				<%
					for (int i = 0; i < rows; i++) {
						String pid = (String) model.getValueAt(i, 0);
				%>
				<div class="divTableRow">
					<%
						for (int j = 0; j < columns; j++) {
								String alignment = j == lastColumn ? "right" : "left";
								String value = (String) model.getValueAt(i, j);
								if (j == 2 && model.hasParser(i)) {
									value = "<a href=\"view-chart?vehicle=" + vehicle_id + "&pid=" + pid + "\">" + value + "</a>";
								}
					%>
					<div class="divTableCell" align=<%=alignment%>><%=value%></div>
					<%
						}
					%>

				</div>
				<%
					}
				%>
			</div>
		</div>
	</div>
</body>
</html>
