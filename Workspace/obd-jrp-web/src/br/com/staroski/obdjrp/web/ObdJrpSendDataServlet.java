package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.io.ByteSerializer;

@WebServlet(name = "SendDataServlet", urlPatterns = { "/send-data" })
@MultipartConfig( //
		location = "T:\\obd-jrp-web\\tmp", //
		fileSizeThreshold = 1024 * 1024, //
		maxFileSize = 1024 * 1024 * 5, //
		maxRequestSize = 1024 * 1024 * 5 * 5 //
)
public final class ObdJrpSendDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		Part part = request.getPart("fileUpload");
		if (part != null) {
			if (savePart(part)) {
				out.write("OK");
			} else {
				out.write("ERROR");
			}
		}
		response.setStatus(HttpURLConnection.HTTP_OK);
	}

	private boolean savePart(Part part) {
		try {
			InputStream input = part.getInputStream();
			Package dataPackage = ByteSerializer.readFrom(input);
			return saveDataPackage(dataPackage);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private File getFile(Package dataPackage, String extension) throws IOException {
		File file = getDataDir(dataPackage);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String name = formatter.format(new Date(dataPackage.getTime()));
		file = new File(file, name + extension);
		file.createNewFile();
		return file;
	}

	private boolean saveDataPackage(Package dataPackage) {
		try {
			File obdFile = getFile(dataPackage, ".obd");
			System.out.printf("Gravando \"%s\"...", obdFile.getAbsolutePath());
			FileOutputStream obdOutput = new FileOutputStream(obdFile);
			ByteSerializer.writeTo(obdOutput, dataPackage);
			obdOutput.close();
			System.out.println("  OK!");
			return true;
		} catch (IOException e) {
			System.out.println("  ERRO!");
			e.printStackTrace();
			return false;
		}
	}

	private File getDataDir(Package dataPackage) {
		String vin = dataPackage.getVIN();
		// String path = getServletContext().getRealPath("/obd-jrp-data/" + vin);
		File file = new File("T:\\obd-jrp-web\\obd-jrp-data", vin);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

}
