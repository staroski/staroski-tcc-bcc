package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.utils.CSV;

public final class ChartBuilder {

	private static final String DIV_CHART_ID_PREFIX = "chart_";
	private static final String DRAW_CHART_PREFIX = "drawChart_";

	private static final SimpleDateFormat dateReader = ObdJrpProperties.DATE_FORMAT;

	private String vehicleId;
	private final String pid;
	private final CSV csv;

	ChartBuilder(String vehicleId, String pid) throws IOException {
		this.vehicleId = vehicleId;
		this.pid = pid;
		this.csv = createCSV();
	}

	public String createChartData(int number) {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("  \"cols\": [{\"label\": \"Moment\", \"type\": \"datetime\"},{\"label\": \"Value\",  \"type\": \"number\"}],\n");
		json.append("  \"rows\": [");
		for (int line = 0, lines = csv.getLines(); line < lines; line++) {
			try {
				String moment = String.format("Date(%d)", dateReader.parse(csv.getValueAt(line, 0)).getTime());
				String value = csv.getValueAt(line, number).replace(',', '.');
				if (line > 0) {
					json.append(",\n         ");
				}
				json.append(String.format("{\"c\":[{\"v\": \"%s\"},{\"v\": \"%s\"}]}", moment, value));
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
		script.append("<script type=\"text/javascript\" src=\"https://code.jquery.com/jquery-3.1.1.min.js\"></script>\n");
		script.append("<script type=\"text/javascript\">\n");
		script.append("google.charts.load('current', {'packages':['corechart']});\n");
		script.append(createChartFunctionCalls());
		script.append(createChartFunctionDeclarations());
		script.append("</script>\n");
		return script.toString();
	}

	private String createChartFunctionCalls() {
		StringBuilder lines = new StringBuilder();
		lines.append(String.format("google.charts.setOnLoadCallback(load_page_data);\n"));
		lines.append(String.format("function load_page_data(){\n"));
		for (int number = 1, size = csv.getHeaders(); number < size; number++) {
			lines.append(String.format("    $.ajax({\n", number));
			lines.append(String.format("        url: 'exec?cmd=ViewChart&vehicle=%s&pid=%s&chart=%d',\n", vehicleId, pid, number));
			lines.append(String.format("        success: function (data) {\n"));
			lines.append(String.format("            %s%03d(data);\n", DRAW_CHART_PREFIX, number, number));
			lines.append(String.format("        },\n"));
			lines.append(String.format("        error: function (error) {\n"));
			lines.append(String.format("            alert('Error:' + error);\n"));
			lines.append(String.format("        }\n"));
			lines.append(String.format("    });\n"));
		}
		lines.append(String.format("}\n"));
		return lines.toString();
	}

	private String createChartFunctionDeclarations() {
		StringBuilder lines = new StringBuilder();
		for (int number = 1, size = csv.getHeaders(); number < size; number++) {
			lines.append(String.format("function %s%03d(chart_data) {\n", DRAW_CHART_PREFIX, number));
			lines.append(String.format("    var data = new google.visualization.DataTable(chart_data);\n"));
			lines.append(String.format("    var options = {\n"));
			lines.append(String.format("        title: '%s',\n", csv.getHeader(number)));
			lines.append(String.format("        curveType: 'function',\n"));
			lines.append(String.format("        legend: { position: 'none' },\n"));
			lines.append(String.format("        enableInteractivity: true,\n"));
			lines.append(String.format("        chartArea: { width: '90%%' },\n"));
			lines.append(String.format("        hAxis: {\n"));
			lines.append(String.format("            gridlines: {\n"));
			lines.append(String.format("                units: {\n"));
			lines.append(String.format("                    days: {format: ['dd/MM']},\n"));
			lines.append(String.format("                    hours: {format: ['HH:mm']},\n"));
			lines.append(String.format("                }\n"));
			lines.append(String.format("            },\n"));
			lines.append(String.format("            minorGridlines: {\n"));
			lines.append(String.format("                units: {\n"));
			lines.append(String.format("                    hours: {format: ['HH:mm']},\n"));
			lines.append(String.format("                    minutes: {format: ['mm:ss']}\n"));
			lines.append(String.format("                }\n"));
			lines.append(String.format("            }\n"));
			lines.append(String.format("        }\n"));
			lines.append(String.format("    };\n"));
			lines.append(String.format("    var chart = new google.visualization.LineChart(document.getElementById('%s%03d'));\n", DIV_CHART_ID_PREFIX, number));
			lines.append(String.format("    chart.draw(data, options);\n"));
			lines.append(String.format("}\n"));
		}
		return lines.toString();
	}

	private CSV createCSV() throws IOException {
		File dir = ObdJrpWeb.getScanDir(vehicleId);
		List<File> files = new ArrayList<>();
		files.addAll(Arrays.asList(dir.listFiles(ObdJrpWeb.SCAN_FILES)));
		Collections.sort(files, ObdJrpWeb.OLD_FILE_FIRST);
		List<Scan> scans = new ArrayList<>();
		for (File file : files) {
			try {
				FileInputStream input = new FileInputStream(file);
				scans.add(Scan.readFrom(input));
				input.close();
			} catch (FileNotFoundException e) {
				// ignorar
			}
		}
		return CSV.createSingleCSV(scans, pid);
	}
}
