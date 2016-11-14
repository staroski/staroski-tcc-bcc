package br.com.staroski.obdjrp.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

final class GetRequest {

	private final String url;
	private final List<String[]> params;

	public GetRequest(String url) {
		this.url = url;
		this.params = new ArrayList<>();
	}

	public void addParam(String name, String value) {
		params.add(new String[] { name, value });
	}

	public List<String> finish() throws IOException {
		URL url = createURL();
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		return Http.readResponse(httpConn);
	}

	private URL createURL() throws IOException {
		StringBuilder text = new StringBuilder(url);
		int count = params.size();
		if (count > 0) {
			text.append("?");
			for (int i = 0; i < count; i++) {
				if (i > 0) {
					text.append("&");
				}
				String[] param = params.get(i);
				String name = param[0];
				String value = param[1];
				text.append(name).append("=").append(value);
			}
		}
		return new URL(text.toString());
	}
}
