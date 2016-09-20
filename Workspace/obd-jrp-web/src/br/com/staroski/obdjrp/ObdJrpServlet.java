package br.com.staroski.obdjrp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ObdJrpServlet", urlPatterns = { "/send-data" })
public final class ObdJrpServlet extends HttpServlet {

	private static final long serialVersionUID = 1;

	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String[]> map = request.getParameterMap();
		PrintWriter writer = response.getWriter();
		writer.append("Parametros recebidos:\n");
		for (String param : map.keySet()) {
			writer.append("\t").append(param).append(" = ").append(Arrays.toString(map.get(param))).append("\n");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}
