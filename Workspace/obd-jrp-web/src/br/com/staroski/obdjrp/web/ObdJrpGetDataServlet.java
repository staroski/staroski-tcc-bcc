package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.utils.Conversions;

@WebServlet(name = "GetDataServlet", urlPatterns = { "/get-data" })
public final class ObdJrpGetDataServlet extends ObdJrpServlet {

	private static final long serialVersionUID = 1;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String vehicle = request.getParameter("vehicle");
		if (vehicle == null || vehicle.isEmpty()) {
			out.println("Availiable vehicles:");
			File dir = getDataDir();
			for (File f : dir.listFiles()) {
				String hexa = f.getName();
				vehicle = new String(Conversions.hexasToBytes(hexa));
				out.printf("<p><a href=?vehicle=%s>%s</a></p>", hexa, vehicle);
			}
		} else {
			request.setAttribute("teste", ObdJrpProperties.get().formatted(new Date()));
			out.println(HtmlChartBuilder.createPage(vehicle));
		}
		response.setStatus(HttpURLConnection.HTTP_OK);
	}

}
