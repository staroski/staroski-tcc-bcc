package br.com.staroski.obdjrp.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

public final class HttpFileUploader {

	public static void upload(String url, File binaryFile) throws IOException {
		final String charset = "UTF-8";
		final String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
		final String CRLF = "\r\n"; // Line separator required by multipart/form-data.

		final URLConnection connection = new URL(url).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		try (OutputStream output = connection.getOutputStream(); PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);) {
			writer.append("--" + boundary).append(CRLF);
			writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
			writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
			writer.append("Content-Transfer-Encoding: binary").append(CRLF);
			writer.append(CRLF).flush();
			Files.copy(binaryFile.toPath(), output);
			output.flush(); // Important before continuing with writer!
			writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

			// End of multipart/form-data.
			writer.append("--" + boundary + "--").append(CRLF).flush();
		}

		// Request is lazily fired whenever you need to obtain information about response.
		int responseCode = ((HttpURLConnection) connection).getResponseCode();
		System.out.println(responseCode); // Should be 200
	}

	private HttpFileUploader() {}

}
