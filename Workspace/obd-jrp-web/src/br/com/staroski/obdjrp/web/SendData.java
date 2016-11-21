package br.com.staroski.obdjrp.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
			organizeScanDir(vehicleId);
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

	private void organizeScanDir(String vehicleId) {
		File dir = ObdJrpWeb.getScanDir(vehicleId);
		List<File> files = new ArrayList<>();
		files.addAll(Arrays.asList(dir.listFiles(ObdJrpWeb.SCAN_FILES)));
		Collections.sort(files, ObdJrpWeb.OLD_FILE_FIRST);
		while (files.size() > ObdJrpWeb.SCANS_SIZE) {
			File scanFile = files.remove(0);
			System.out.printf("Apagando \"%s\"...%n", scanFile.getAbsolutePath());
			if (scanFile.delete()) {
				System.out.println("OK");
			} else {
				System.out.println("ERRO");
			}
		}
	}

	private boolean saveScan(String vehicleId, Scan scan) {
		try {
			File scanFile = getFile(vehicleId, scan, ".scan");
			System.out.printf("Gravando \"%s\"...%n", scanFile.getAbsolutePath());
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
