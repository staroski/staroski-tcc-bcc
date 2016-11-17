package br.com.staroski.obdjrp.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import br.com.staroski.obdjrp.ObdJrpProperties;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.utils.CSV;
import br.com.staroski.obdjrp.utils.Conversions;

final class UploadData implements Command {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		Part uploadedFile = request.getPart("fileUpload");
		if (uploadedFile != null && savePart(uploadedFile)) {
			out.println("OK");
		} else {
			out.println("ERROR");
		}
		response.setStatus(HttpURLConnection.HTTP_OK);
		return null;
	}

	private File getFile(Package dataPackage, String extension) throws IOException {
		String vehicle = dataPackage.getVehicle();
		String vehicle_id = Conversions.bytesToHexas(vehicle.getBytes());
		File file = ObdJrpWeb.getVehicleDir(vehicle_id);
		String name = ObdJrpProperties.get().formatted(new Date(dataPackage.getTime()));
		file = new File(file, name + extension);
		file.createNewFile();
		return file;
	}

	private boolean saveDataPackage(Package dataPackage) {
		try {
			File obdFile = getFile(dataPackage, ".obd");
			System.out.printf("Gravando \"%s\"...", obdFile.getAbsolutePath());
			FileOutputStream obdOutput = new FileOutputStream(obdFile);
			dataPackage.writeTo(obdOutput);
			obdOutput.close();
			obdFile.setLastModified(dataPackage.getTime());
			System.out.println("  OK!");

			File csvFile = getFile(dataPackage, ".csv");
			System.out.printf("Gravando \"%s\"...", csvFile.getAbsolutePath());
			FileOutputStream csvOutput = new FileOutputStream(csvFile);
			CSV.writeTo(csvOutput, dataPackage);
			csvOutput.close();
			csvFile.setLastModified(dataPackage.getTime());
			System.out.println("  OK!");

			return true;
		} catch (IOException e) {
			System.out.println("  ERRO!");
			e.printStackTrace();
			return false;
		}
	}

	private boolean savePart(Part part) {
		try {
			InputStream input = part.getInputStream();
			Package dataPackage = Package.readFrom(input);
			return saveDataPackage(dataPackage);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
