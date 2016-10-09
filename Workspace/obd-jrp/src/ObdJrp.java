import javax.swing.JFrame;
import javax.swing.UIManager;

import br.com.staroski.obdjrp.ui.UIController;

public final class ObdJrp {

	public static void main(String[] args) {
		try {
			ObdJrp obdJrp = new ObdJrp();
			obdJrp.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private ObdJrp() {}

	private void execute() {
		startDataDirMonitor();
		openMainFrame();
	}

	private void openMainFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		try {
			JFrame frame = UIController.get().getMainFrame();
			frame.setVisible(true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void startDataDirMonitor() {
		// TODO Auto-generated method stub
	}
}
