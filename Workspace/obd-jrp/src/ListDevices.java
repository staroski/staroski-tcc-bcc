import java.io.IOException;
import java.util.List;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import br.com.staroski.obdjrp.io.bluetooth.Bluetooth;

public final class ListDevices {

	public static void main(String[] args) {
		try {
			ListDevices deviceSearch = new ListDevices();
			deviceSearch.execute();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private ListDevices() {}

	private void execute() throws IOException {
		List<RemoteDevice> remoteDevices = Bluetooth.getRemoteDevices();
		for (RemoteDevice remoteDevice : remoteDevices) {
			System.out.printf("device \"%s\" - \"%s\" {", remoteDevice.getBluetoothAddress(), remoteDevice.getFriendlyName(false));
			printServices(Bluetooth.getServices(remoteDevice));
			System.out.printf("}%n%n");
		}
	}

	private void printServices(List<ServiceRecord> services) {
		for (ServiceRecord service : services) {
			System.out.printf("%n\t\"%s\"%n", Bluetooth.getName(service));
		}
	}
}
