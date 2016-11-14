package br.com.staroski.obdjrp.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public final class Http {

	public static boolean sendGetRequest(String url, String[][] params) throws IOException {
		GetRequest get = new GetRequest(url);
		for (String[] param : params) {
			get.addParam(param[0], param[1]);
		}
		List<String> response = get.finish();
		boolean accepted = response.size() == 1 && "OK".equals(response.get(0));
		return accepted;
	}

	public static boolean sendPostRequest(String url, File file) throws IOException {
		PostRequest post = new PostRequest(url);
		post.addFilePart("fileUpload", file);
		List<String> response = post.finish();
		boolean accepted = response.size() == 1 && "OK".equals(response.get(0));
		return accepted;
	}

	static List<String> readResponse(HttpURLConnection httpConn) throws IOException {
		List<String> response = new ArrayList<>();
		int status = httpConn.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				response.add(line);
			}
			reader.close();
			httpConn.disconnect();
			return response;
		} else {
			throw new IOException("Server returned non-OK status: " + status);
		}
	}

	private Http() {}
}
