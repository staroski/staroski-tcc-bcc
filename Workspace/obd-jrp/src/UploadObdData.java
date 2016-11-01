import br.com.staroski.obdjrp.FolderMonitor;

public final class UploadObdData {

	public static void main(String[] args) {
		try {
			UploadObdData uploader = new UploadObdData();
			uploader.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private UploadObdData() {}

	private void execute() {
		try {
			new FolderMonitor().start();
		} catch (Exception error) {
			System.out.printf("%s aborted!%n%s:%s%n", //
					FolderMonitor.class.getSimpleName(), //
					error.getClass().getSimpleName(), //
					error.getMessage());
		}
	}
}
