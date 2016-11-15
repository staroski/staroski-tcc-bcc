package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.FileInputStream;
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

@WebServlet(name = "GetDataServlet", urlPatterns = { "/get-data" })
public final class GetDataServlet extends ObdJrpServlet {

	private static final long serialVersionUID = 1;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String vehicleId = request.getParameter("vehicle");
		if (vehicleId == null || vehicleId.isEmpty()) {
			RequestDispatcher view = request.getRequestDispatcher("index.jsp");
			view.forward(request, response);
			return;
		}
		request.setAttribute("vehicle", new String(Conversions.hexasToBytes(vehicleId)));
		request.setAttribute("chart_builder", new HtmlChartBuilder(createCSV(vehicleId)));

		// out.println(HtmlChartBuilder.createPage(vehicle));

		RequestDispatcher view = request.getRequestDispatcher("vehicle-detail.jsp");
		view.forward(request, response);

	}

	private CSV createCSV(String vehicleId) throws IOException {
		File dir = getScanDir(vehicleId);
		List<File> files = new ArrayList<>();
		files.addAll(Arrays.asList(dir.listFiles(SCAN_FILES)));
		Collections.sort(files, OLDEST_FILE);
		List<Scan> scans = new ArrayList<>();
		for (File file : files) {
			scans.add(Scan.readFrom(new FileInputStream(file)));
		}
		return CSV.createSingleCSV(scans);
	}

}
