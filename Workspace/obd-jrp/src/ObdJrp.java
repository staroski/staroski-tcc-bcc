import javax.swing.JFrame;

import br.com.staroski.obdjrp.ui.ScreenController;

public class ObdJrp {

	public static void main(String[] args) {
		try {
			JFrame frame = ScreenController.get().getMainFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
