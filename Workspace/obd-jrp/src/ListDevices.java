import java.io.IOException;
import java.util.List;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import br.com.staroski.obdjrp.io.bluetooth.Bluetooth;

public class ListDevices {

	public static void main(String[] args) {
		try {
			new ListDevices().execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void execute() throws IOException {
		List<RemoteDevice> devices = Bluetooth.getDevices();
		for (RemoteDevice device : devices) {
			System.out.printf("device \"%s\" {%n", device.getBluetoothAddress());
			List<ServiceRecord> services = Bluetooth.getServices(device);
			for (ServiceRecord service : services) {
				System.out.printf("\t\"%s\"%n", Bluetooth.getName(service));
			}
			System.out.printf("}%n%n");
		}
	}

}
