package br.com.staroski.obdjrp.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.utils.Conversions;

@WebServlet(name = "SendScanServlet", urlPatterns = { "/send-scan" })
public final class SendScanServlet extends ObdJrpServlet {

	private static final long serialVersionUID = 1;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			String paramVehicle = request.getParameter("vehicle");
			String paramScan = request.getParameter("scan");
			byte[] bytes = Conversions.hexasToBytes(paramVehicle);
			String vehicle = new String(bytes);
			bytes = Conversions.hexasToBytes(paramScan);
			Scan scan = Scan.readFrom(new ByteArrayInputStream(bytes));
			System.out.printf("Vehicle: %s - Scan: %s%n", vehicle, scan);
			out.println("OK");
		} catch (Exception error) {
			System.out.printf("%s: %s%n", //
					error.getClass().getSimpleName(), //
					error.getMessage());
			out.println("ERROR");
		}
		response.setStatus(HttpURLConnection.HTTP_OK);
	}

}
