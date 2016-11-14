package br.com.staroski.obdjrp.web;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.utils.CSV;

final class HtmlChartBuilder {

	private static final String DIV_DATA_ID = "data";
	private static final String DIV_CHART_ID_PREFIX = "chart_";
	private static final String DRAW_CHART_PREFIX = "drawChart_";

	private static final SimpleDateFormat dateReader = ObdJrpProperties.DATE_FORMAT;
	private static final SimpleDateFormat dateWriter = new SimpleDateFormat("'new Date('yyyy', 'MM', 'dd', 'HH', 'mm', 'ss', 'SSS')'");

	public static String createPage(String vehicle) {
		File dir = new File(ObdJrpServlet.getDataDir(), vehicle);
		CSV csv = CSV.createSingleCSV(dir);
		if (csv.isEmpty()) {
			return "There is no data for vehicle " + vehicle;
		}
		return new HtmlChartBuilder(csv).getChartPage();
	}

	private final CSV csv;

	private HtmlChartBuilder(CSV csv) {
		this.csv = csv;
	}

	private String createChartData(int number) {
		StringBuilder chart = new StringBuilder();
		chart.append(String.format("var data = new google.visualization.DataTable();\n"));
		chart.append(String.format("data.addColumn('datetime', 'Moment');\n"));
		chart.append(String.format("data.addColumn('number', 'Value');\n"));
		chart.append(String.format("data.addRows([\n"));
		for (int line = 0, lines = csv.getLines(); line < lines; line++) {
			try {
				Date moment = dateReader.parse(csv.getValueAt(line, 0));
				String value = csv.getValueAt(line, number).replace(',', '.');
				chart.append(String.format("[%s, %s],\n", dateWriter.format(moment), value));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		chart.append(String.format("]);\n"));
		return chart.toString();
	}

	private String createChartFunctionCalls() {
		StringBuilder calls = new StringBuilder();
		for (int number = 1, size = csv.getHeaders(); number < size; number++) {
			calls.append(String.format("google.charts.setOnLoadCallback(%s%03d);\n", DRAW_CHART_PREFIX, number));
		}
		return calls.toString();
	}

	private String createChartFunctionDeclarations() {
		StringBuilder declarations = new StringBuilder();
		for (int number = 1, size = csv.getHeaders(); number < size; number++) {
			declarations.append(String.format("function %s%03d() {\n", DRAW_CHART_PREFIX, number));
			declarations.append(createChartData(number));
			declarations.append(createChartOptions(number));
			declarations.append(String.format("}\n"));
		}
		return declarations.toString();
	}

	private String createChartOptions(int number) {
		StringBuilder options = new StringBuilder();
		options.append(String.format("var options = {\n"));
		options.append(String.format("title: 'Sensor %d - %s',\n", number, csv.getHeader(number)));
		options.append(String.format("curveType: 'function',\n"));
		options.append(String.format("legend: {position: 'none'},\n"));
		options.append(String.format("enableInteractivity: true,\n"));
		options.append(String.format("chartArea: {\n"));
		options.append(String.format("width: '85%%'\n"));
		options.append(String.format("},\n"));
		options.append(String.format("hAxis: {\n"));
		options.append(String.format("gridlines: {\n"));
		options.append(String.format("units: {\n"));
		options.append(String.format("days: {format: ['dd/MM']},\n"));
		options.append(String.format("hours: {format: ['HH:mm']},\n"));
		options.append(String.format("}\n"));
		options.append(String.format("},\n"));
		options.append(String.format("minorGridlines: {\n"));
		options.append(String.format("units: {\n"));
		options.append(String.format("hours: {format: ['HH:mm']},\n"));
		options.append(String.format("minutes: {format: ['mm:ss']}\n"));
		options.append(String.format("}\n"));
		options.append(String.format("}\n"));
		options.append(String.format("}\n"));
		options.append(String.format("};\n"));
		options.append(String.format("var chart = new google.visualization.LineChart(\n"));
		options.append(String.format("document.getElementById('%s%03d'));\n", DIV_CHART_ID_PREFIX, number));
		options.append(String.format("chart.draw(data, options);\n"));

		return options.toString();
	}

	private String createTagBody() {
		StringBuilder body = new StringBuilder();
		body.append("<body>\n");
		body.append(createTagDiv_data());
		body.append(createTagDiv_chart());
		body.append("</body>\n");
		return body.toString();
	}

	private String createTagDiv_chart() {
		StringBuilder div = new StringBuilder();
		for (int number = 1, size = csv.getHeaders(); number < size; number++) {
			div.append(String.format("<div id=\"%s%03d\"></div>\n", DIV_CHART_ID_PREFIX, number));
		}
		return div.toString();
	}

	private Object createTagDiv_data() {
		StringBuilder div = new StringBuilder();
		String value = "${sessionScope.teste}";
		div.append(String.format("<div id=%s>%s</div>\n", DIV_DATA_ID, value));
		return div.toString();
	}

	private String createTagHead() {
		StringBuilder head = new StringBuilder();
		head.append("<head>\n");
		head.append(createTagScriptGoogle());
		head.append(createTagScriptTimer());
		head.append("</head>\n");
		return head.toString();
	}

	private String createTagHtml() {
		StringBuilder html = new StringBuilder();
		html.append("<html>\n");
		html.append(createTagHead());
		html.append(createTagBody());
		html.append("</html>\n");
		return html.toString();
	}

	private String createTagScriptGoogle() {
		StringBuilder script = new StringBuilder();
		script.append("<script type=\"text/javascript\" src=\"loader.js\"></script>\n");
		script.append("<script type=\"text/javascript\">\n");
		script.append("google.charts.load('current', {'packages':['corechart']});\n");
		script.append(createChartFunctionCalls());
		script.append(createChartFunctionDeclarations());
		script.append("</script>\n");
		return script.toString();
	}

	private String createTagScriptTimer() {
		StringBuilder script = new StringBuilder();
		script.append("<script type=\"text/javascript\">\n");
		script.append("		    var timer = {\n");
		script.append("			    interval: null,\n");
		script.append("			    seconds: 5,\n");
		script.append("			    start: function () {\n");
		script.append("			        var self = this,\n");
		script.append("			            el = document.getElementById('" + DIV_DATA_ID + "');\n");
		script.append("			        el.innerText = this.seconds; // Output initial value\n");
		script.append("			        this.interval = setInterval(function () {\n");
		script.append("			            self.seconds--;\n");
		script.append("			            if (self.seconds == 0) {\n");
		script.append("			                window.location.reload();\n");
		script.append("                     }\n");
		script.append("			            el.innerText = self.seconds;\n");
		script.append("			        }, 1000);\n");
		script.append("			    },");
		script.append("			    stop: function () {");
		script.append("			        window.clearInterval(this.interval)");
		script.append("			    }");
		script.append("			}");
		script.append("			timer.start();");
		script.append("</script>");
		return script.toString();
	}

	private String getChartPage() {
		StringBuilder page = new StringBuilder();
		page.append(createTagHtml());
		return page.toString();
	}
}
