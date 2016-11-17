package br.com.staroski.obdjrp.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.utils.Conversions;

final class ViewChart implements Command {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String vehicleId = request.getParameter("vehicle");
		String pid = request.getParameter("pid");
		String chart = request.getParameter("chart");
		if (vehicleId == null || vehicleId.isEmpty()) {
			return null;
		}
		if (pid == null || pid.isEmpty()) {
			return null;
		}

		Scan lastScan = ObdJrpWeb.getLastScan(vehicleId);
		ChartBuilder chartBuilder = new ChartBuilder(vehicleId, pid);

		if (chart != null && !chart.isEmpty()) {
			String data = chartBuilder.createChartData(Integer.parseInt(chart));
			response.getWriter().write(data);
			return null;
		}

		request.setAttribute("vehicle", vehicleId);
		request.setAttribute("vehicle_description", new String(Conversions.hexasToBytes(vehicleId)));
		request.setAttribute("scan_time", lastScan.getTime());
		request.setAttribute("chart_builder", chartBuilder);
		return "vehicle-chart.jsp";
	}

}
