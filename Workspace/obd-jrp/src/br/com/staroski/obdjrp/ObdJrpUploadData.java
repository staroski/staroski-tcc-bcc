package br.com.staroski.obdjrp;
import java.io.IOException;

import br.com.staroski.obdjrp.core.DataMonitor;
import br.com.staroski.obdjrp.utils.Print;

public final class ObdJrpUploadData extends ObdJrpApp {

	public static void main(String[] args) {
		try {
			ObdJrpUploadData uploader = new ObdJrpUploadData();
			uploader.execute();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
	}

	private ObdJrpUploadData() throws IOException {
		super("upload-data");
	}

	private void execute() {
		try {
			new DataMonitor().start();
		} catch (Exception error) {
			Print.message(error);
		}
	}
}
