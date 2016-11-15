package br.com.staroski.obdjrp.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.utils.Conversions;

@WebServlet(name = "ReadDataServlet", urlPatterns = { "/read-data" })
public final class ReadDataServlet extends ObdJrpServlet {

	private static final long serialVersionUID = 1;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String vehicleId = request.getParameter("vehicle");
		if (vehicleId == null || vehicleId.isEmpty()) {
			return;
		}
		Scan lastScan = getLastScan(vehicleId);
		request.setAttribute("vehicle", vehicleId);
		request.setAttribute("vehicle_description", new String(Conversions.hexasToBytes(vehicleId)));
		request.setAttribute("scan_time", lastScan.getTime());
		request.setAttribute("scan_table_model", new ScanTableModel(lastScan));
		RequestDispatcher view = request.getRequestDispatcher("vehicle-detail.jsp");
		view.forward(request, response);
	}
}
