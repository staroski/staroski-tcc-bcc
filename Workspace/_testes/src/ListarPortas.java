import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ListarPortas {

	public static void main(String[] args) {
		try {
			new ListarPortas().execute();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	private void execute() {
		try {
			Preferences systemRoot = Preferences.userRoot();
			Preferences node = systemRoot.node("HKEY_LOCAL_MACHINE\\HARDWARE\\DEVICEMAP\\SERIALCOMM");
			String[] keys = node.childrenNames();
			for (String key : keys) {
				System.out.println(key + ": " + node.get(key, null));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
