package br.com.staroski.obdjrp.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ObdJrpServlet", urlPatterns = { "/exec" })
@MultipartConfig( //
		location = "tmp", //
		fileSizeThreshold = 1024 * 1024, // 1MB
		maxFileSize = 1024 * 1024 * 5, // 5MB
		maxRequestSize = 1024 * 1024 * 5 * 5 // 25MB
)
public final class ObdJrpServlet extends HttpServlet {

	private static final long serialVersionUID = 1;

	private static final Map<String, Command> COMMANDS = new HashMap<>();

	static {
		COMMANDS.put("ListVehicles", new ListVehicles());
		COMMANDS.put("ReadData", new ReadData());
		COMMANDS.put("SendData", new SendData());
		COMMANDS.put("UploadData", new UploadData());
		COMMANDS.put("ViewChart", new ViewChart());
	}

	private Command getCommand(HttpServletRequest request) {
		Command command = COMMANDS.get(request.getParameter("cmd"));
		return command == null ? Command.NULL : command;
	}

	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Command command = getCommand(request);
		String result = command.execute(request, response);
		if (result != null) {
			response.setHeader("Cache-Control", "no-cache");
			request.getRequestDispatcher(result).forward(request, response);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
}
