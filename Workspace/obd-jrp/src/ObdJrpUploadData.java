import br.com.staroski.obdjrp.ObdJrpFolderMonitor;

public final class ObdJrpUploadData {

	public static void main(String[] args) {
		try {
			ObdJrpUploadData uploader = new ObdJrpUploadData();
			uploader.execute();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
	}

	private ObdJrpUploadData() {}

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
