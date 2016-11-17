package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.utils.CSV;
import br.com.staroski.obdjrp.utils.Conversions;

final class ViewChart implements Command {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String vehicleId = request.getParameter("vehicle");
		if (vehicleId == null || vehicleId.isEmpty()) {
			return null;
		}
		String pid = request.getParameter("pid");
		if (pid == null || pid.isEmpty()) {
			return null;
		}
		Scan lastScan = ObdJrpWeb.getLastScan(vehicleId);
		request.setAttribute("vehicle", vehicleId);
		request.setAttribute("vehicle_description", new String(Conversions.hexasToBytes(vehicleId)));
		request.setAttribute("scan_time", lastScan.getTime());
		request.setAttribute("chart_builder", new HtmlChartBuilder(createCSV(vehicleId, pid)));
		return "vehicle-chart.jsp";
	}

	private CSV createCSV(String vehicleId, String pid) throws IOException {
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
