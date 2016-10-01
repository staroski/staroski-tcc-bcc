package br.com.staroski.obdjrp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

// web.xml
//
//<multipart-config>
//<location>/tmp</location>
//<max-file-size>20848820</max-file-size>
//<max-request-size>418018841</max-request-size>
//<file-size-threshold>1048576</file-size-threshold>
//</multipart-config

@WebServlet(name = "ObdJrpServlet", urlPatterns = { "/send-data" })
@MultipartConfig(location = "T:\\obd-jrp-web\\tmp", fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public final class ObdJrpServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// getServletContext().getRequestDispatcher("/WEB-INF/form.jsp").forward(request, response);
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		Collection<Part> parts = request.getParts();

		System.out.println("<h2> Total parts : " + parts.size() + "</h2>");

		for (Part part : parts) {
			printEachPart(part);
			String fileName = getFileName(part);
			System.out.println(fileName);
			out.write(fileName + " processed!");

			// part.write(getFileName(part));
		}
		response.setStatus(HttpURLConnection.HTTP_OK);
	}

	private void printEachPart(Part part) {
		StringBuffer sb = new StringBuffer();
		sb.append("<p>");
		sb.append("Name : " + part.getName());
		sb.append("<br>");
		sb.append("Content Type : " + part.getContentType());
		sb.append("<br>");
		sb.append("Size : " + part.getSize());
		sb.append("<br>");
		for (String header : part.getHeaderNames()) {
			sb.append(header + " : " + part.getHeader(header));
			sb.append("<br>");
		}
		sb.append("</p>");
		System.out.println(sb.toString());

	}

	private String getFileName(Part part) {
		String partHeader = part.getHeader("content-disposition");
		for (String cd : partHeader.split(";")) {
			if (cd.trim().startsWith("filename")) {
				return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}
}
