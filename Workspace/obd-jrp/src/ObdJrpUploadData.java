import br.com.staroski.obdjrp.FolderMonitor;

public final class ObdJrpUploadData {

	public static void main(String[] args) {
		try {
			ObdJrpUploadData uploader = new ObdJrpUploadData();
			uploader.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private ObdJrpUploadData() {}

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
