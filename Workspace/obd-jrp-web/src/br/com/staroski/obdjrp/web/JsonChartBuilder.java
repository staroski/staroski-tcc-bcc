package br.com.staroski.obdjrp.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.utils.CSV;

public final class JsonChartBuilder {

	private static final String DIV_CHART_ID_PREFIX = "chart_";
	private static final String DRAW_CHART_PREFIX = "drawChart_";

	private static final SimpleDateFormat dateReader = ObdJrpProperties.DATE_FORMAT;
	private static final SimpleDateFormat dateWriter = new SimpleDateFormat("'new Date('yyyy', 'MM', 'dd', 'HH', 'mm', 'ss', 'SSS')'");

	private final CSV csv;

	public JsonChartBuilder(CSV csv) {
		this.csv = csv;
	}

	public String createJsonChartData(int number) {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("  cols: [{label: 'Moment', type: 'datetime'},{label: 'Value',  type: 'number'}],\n");
		json.append("  rows: [");
		for (int line = 0, lines = csv.getLines(); line < lines; line++) {
			try {
				String moment = dateWriter.format(dateReader.parse(csv.getValueAt(line, 0)));
				String value = csv.getValueAt(line, number).replace(',', '.');
				if (line > 0) {
					json.append(",\n         ");
				}
				json.append(String.format("{c:[{v: %s},{v: %s}]}", moment, value));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		json.append("]\n");
		json.append("}");
		return json.toString();
	}

	public String createTagDiv() {
		StringBuilder div = new StringBuilder();
		for (int number = 1, size = csv.getHeaders(); number < size; number++) {
			div.append(String.format("<div id=\"%s%03d\"></div>\n", DIV_CHART_ID_PREFIX, number));
		}
		return div.toString();
	}

	public String createTagScript() {
		StringBuilder script = new StringBuilder();
		script.append("<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n");
		script.append("<script type=\"text/javascript\">\n");
		script.append("google.charts.load('current', {'packages':['corechart']});\n");
		script.append(createChartFunctionCalls());
		script.append(createChartFunctionDeclarations());
		script.append("</script>\n");
		return script.toString();
	}

	private String createChartDataTable(int number) {
		StringBuilder chart = new StringBuilder();
		String json = createJsonChartData(number);
		chart.append(String.format("var data = new google.visualization.DataTable(%s);\n", json));
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
			declarations.append(createChartDataTable(number));
			declarations.append(createChartOptions(number));
			declarations.append(String.format("}\n"));
		}
		return declarations.toString();
	}

	private String createChartOptions(int number) {
		StringBuilder options = new StringBuilder();
		options.append(String.format("var options = {\n"));
		options.append(String.format("title: '%s',\n", csv.getHeader(number)));
		options.append(String.format("curveType: 'function',\n"));
		options.append(String.format("legend: {position: 'none'},\n"));
		options.append(String.format("enableInteractivity: true,\n"));
		options.append(String.format("chartArea: {\n"));
		options.append(String.format("width: '90%%'\n"));
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
}
