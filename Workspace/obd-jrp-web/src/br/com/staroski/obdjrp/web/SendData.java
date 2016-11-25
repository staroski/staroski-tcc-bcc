package br.com.staroski.obdjrp.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.staroski.obdjrp.core.Config;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.utils.Conversions;

final class SendData implements Command {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			String vehicleId = request.getParameter("vehicle");
			byte[] scanBytes = Conversions.hexasToBytes(request.getParameter("scan"));
			saveScan(vehicleId, Scan.readFrom(new ByteArrayInputStream(scanBytes)));
			ObdJrpWeb.organizeScanDir(vehicleId);
			out.println("OK");
		} catch (Exception error) {
			System.out.printf("%s: %s%n", //
					error.getClass().getSimpleName(), //
					error.getMessage());
			out.println("ERROR");
		}
		return null;
	}

	private File getFile(String vehicleId, Scan scan, String extension) throws IOException {
		File dir = ObdJrpWeb.getScanDir(vehicleId);
		String name = Config.get().formatted(new Date(scan.getTime()));
		File file = new File(dir, name + extension);
		file.createNewFile();
		return file;
	}

	private boolean saveScan(String vehicleId, Scan scan) {
		try {
			File scanFile = getFile(vehicleId, scan, ".scan");
			System.out.printf("Saving \"%s\"...%n", scanFile.getAbsolutePath());
			FileOutputStream scanOutput = new FileOutputStream(scanFile);
			scan.writeTo(scanOutput);
			scanOutput.close();
			scanFile.setLastModified(scan.getTime());
			System.out.println("OK!");
			return true;
		} catch (IOException e) {
			System.out.println("ERRO!");
			e.printStackTrace();
			return false;
		}
	}
}
