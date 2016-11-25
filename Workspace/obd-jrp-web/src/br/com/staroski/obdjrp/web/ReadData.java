package br.com.staroski.obdjrp.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.staroski.obdjrp.data.Scan;

final class ReadData implements Command {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String vehicleId = request.getParameter("vehicle");
		if (vehicleId == null || vehicleId.isEmpty()) {
			return null;
		}

		String history = request.getParameter("history");
		if ("yes".equalsIgnoreCase(history)) {
			String pid = request.getParameter("pid");
			List<Scan> scans = ObdJrpWeb.getAllScans(vehicleId);
			request.setAttribute("vehicle", vehicleId);
			request.setAttribute("pid", pid);
			request.setAttribute("history_table_model", new HistoryTableModel(pid, scans));
			return "vehicle-history.jsp";
		}

		Scan lastScan = ObdJrpWeb.getLastScan(vehicleId);
		request.setAttribute("vehicle", vehicleId);
		request.setAttribute("scan_table_model", new ScanTableModel(lastScan));
		return "vehicle-detail.jsp";
	}

}
