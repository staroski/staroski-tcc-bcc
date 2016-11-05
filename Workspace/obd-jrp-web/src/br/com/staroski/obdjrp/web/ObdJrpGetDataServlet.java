package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "GetDataServlet", urlPatterns = { "/get-data" })
public final class ObdJrpGetDataServlet extends ObdJrpServlet {

	private static final long serialVersionUID = 1;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String vin = request.getParameter("vehicle");
		if (vin == null || vin.isEmpty()) {
			out.println("Availiable vehicles:");
			File dir = getDataDir();
			for (File f : dir.listFiles()) {
				out.println("\t" + f.getName());
			}
		} else {
			out.println(HtmlChartBuilder.createPage(vin));
		}
		response.setStatus(HttpURLConnection.HTTP_OK);
	}

}
