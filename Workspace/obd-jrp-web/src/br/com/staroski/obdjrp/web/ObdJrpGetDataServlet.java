package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "GetDataServlet", urlPatterns = { "/get-data" })
public class ObdJrpGetDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("Availiable VIMs:");
		File dir = getDataDir();
		for (File f : dir.listFiles()) {
			out.println("\t" + f.getName());
		}

		response.setStatus(HttpURLConnection.HTTP_OK);
	}

	private File getDataDir() {
		File file = new File("T:\\obd-jrp-web\\obd-jrp-data");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
}
