package br.com.staroski.obdjrp.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.utils.Conversions;

final class ReadData implements Command {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String vehicleId = request.getParameter("vehicle");
		if (vehicleId == null || vehicleId.isEmpty()) {
			return null;
		}
		Scan lastScan = ObdJrpWeb.getLastScan(vehicleId);
		request.setAttribute("vehicle", vehicleId);
		request.setAttribute("vehicle_description", new String(Conversions.hexasToBytes(vehicleId)));
		request.setAttribute("scan_time", lastScan.getTime());
		request.setAttribute("scan_table_model", new ScanTableModel(lastScan));
		return "vehicle-detail.jsp";
	}

}
