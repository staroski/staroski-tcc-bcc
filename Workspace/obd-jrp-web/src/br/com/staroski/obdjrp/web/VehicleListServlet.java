package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.staroski.obdjrp.utils.Conversions;

@WebServlet(name = "VehicleListServlet", urlPatterns = { "/index.jsp" })
public final class VehicleListServlet extends ObdJrpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String[]> vehicles = new ArrayList<>();
		File dir = getDataDir();
		for (File f : dir.listFiles()) {
			String hexa = f.getName();
			String vehicle = new String(Conversions.hexasToBytes(hexa));
			vehicles.add(new String[] { hexa, vehicle });
		}
		request.setAttribute("vehicles", vehicles);
		RequestDispatcher view = request.getRequestDispatcher("vehicle-list.jsp");
		response.setHeader("Cache-Control", "no-cache");
		view.forward(request, response);
	}
}
