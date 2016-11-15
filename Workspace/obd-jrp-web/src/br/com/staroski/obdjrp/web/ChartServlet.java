package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.utils.CSV;
import br.com.staroski.obdjrp.utils.Conversions;

@WebServlet(name = "ChartServlet", urlPatterns = { "/view-chart" })
public final class ChartServlet extends ObdJrpServlet {

	private static final long serialVersionUID = 1;

	private CSV createCSV(String vehicleId, String pid) throws IOException {
		File dir = getScanDir(vehicleId);
		List<File> files = new ArrayList<>();
		files.addAll(Arrays.asList(dir.listFiles(SCAN_FILES)));
		Collections.sort(files, OLDEST_FILE);
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

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String vehicleId = request.getParameter("vehicle");
		if (vehicleId == null || vehicleId.isEmpty()) {
			return;
		}
		String pid = request.getParameter("pid");
		if (pid == null || pid.isEmpty()) {
			return;
		}
		Scan lastScan = getLastScan(vehicleId);
		request.setAttribute("vehicle", vehicleId);
		request.setAttribute("vehicle_description", new String(Conversions.hexasToBytes(vehicleId)));
		request.setAttribute("scan_time", lastScan.getTime());
		request.setAttribute("chart_builder", new HtmlChartBuilder(createCSV(vehicleId, pid)));
		RequestDispatcher view = request.getRequestDispatcher("vehicle-chart.jsp");
		view.forward(request, response);
	}
}
