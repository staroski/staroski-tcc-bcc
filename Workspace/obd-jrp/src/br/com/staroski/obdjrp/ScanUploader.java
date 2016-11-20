package br.com.staroski.obdjrp;

import java.io.ByteArrayOutputStream;

import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;
import br.com.staroski.obdjrp.http.Http;
import br.com.staroski.obdjrp.utils.Conversions;

final class ScanUploader extends ObdJrpAdapter {

	private final PackagePersister persister;

	ScanUploader() {
		this.persister = new PackagePersister();
	}

	@Override
	public void onError(ELM327Error error) {
		persister.persist();
	}

	@Override
	public void onScanned(final Scan scan) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					tryUpload(scan);
				} catch (Exception error) {
					System.out.printf("%s: %s%n", //
							error.getClass().getSimpleName(), //
							error.getMessage());
				}
			}
		}, "ScanUploader_Thread");
		thread.start();
	}

	private void tryUpload(Scan scan) {
		if (upload(scan)) {
			persister.persist();
		} else {
			persister.add(scan);
		}
	}

	private boolean upload(Scan dataScan) {
		System.out.printf("sending scan to server%n");
		try {
			String url = ObdJrpProperties.get().webServer() + "/exec?cmd=SendData";
			byte[] bytes = ObdJrpProperties.get().vehicle().getBytes();
			String vehicle = Conversions.bytesToHexas(bytes);
			bytes = dataScan.writeTo(new ByteArrayOutputStream()).toByteArray();
			String scan = Conversions.bytesToHexas(bytes);
			String[][] params = new String[][] { //
					new String[] { "vehicle", vehicle }, //
					new String[] { "scan", scan }, //
			};
			boolean accepted = Http.sendGetRequest(url, params);
			System.out.printf("scan %s by server%n", accepted ? "accepted" : "rejected");
			return accepted;
		} catch (Exception error) {
			System.out.printf("%s: %s%n", //
					error.getClass().getSimpleName(), //
					error.getMessage());
			return false;
		}
	}
}
