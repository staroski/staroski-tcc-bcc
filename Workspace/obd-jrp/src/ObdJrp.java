import javax.swing.JFrame;

import br.com.staroski.obdjrp.ui.OBD2Frame;

public class ObdJrp {

	public static void main(String[] args) {
		try {
			OBD2Frame frame = new OBD2Frame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
