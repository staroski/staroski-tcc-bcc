import java.io.IOException;

import br.com.staroski.obdjrp.ObdJrpFolderMonitor;

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
			new ObdJrpFolderMonitor().start();
		} catch (Exception error) {
			System.out.printf("%s aborted!%n%s:%s%n", //
					ObdJrpFolderMonitor.class.getSimpleName(), //
					error.getClass().getSimpleName(), //
					error.getMessage());
		}
	}
}
