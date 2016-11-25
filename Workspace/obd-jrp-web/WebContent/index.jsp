<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- <meta http-equiv="refresh" content="0; url=exec?cmd=ListVehicles" /> -->
<title>OBD-JRP</title>
</head>
<body>
	<!-- exec?cmd=ReadData&vehicle=53696D756C61646F72204D6F746F2047 -->
	<div id="title" align="center">
		<h1>OBD-JRP</h1>
	</div>
	<form action="exec">
		<input type="hidden" name="cmd" value="ReadData" />
		<div align="center">
			Vehicle: <input type="text" name="vehicle" value="" size="40"
				required="required" /> <input type="submit" value="View" />
		</div>
	</form>
</body>
</html>
