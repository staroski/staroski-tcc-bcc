import java.io.IOException;
import java.util.List;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import br.com.staroski.obdjrp.io.bluetooth.Bluetooth;

public final class ObdJrpListDevices {

	public static void main(String[] args) {
		try {
			ObdJrpListDevices deviceSearch = new ObdJrpListDevices();
			deviceSearch.execute();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
	}

	private ObdJrpListDevices() {}

	private void execute() throws IOException {
		List<RemoteDevice> remoteDevices = Bluetooth.listDevices();
		for (RemoteDevice remoteDevice : remoteDevices) {
			System.out.printf("device \"%s\" - \"%s\" {", remoteDevice.getBluetoothAddress(), remoteDevice.getFriendlyName(false));
			printServices(Bluetooth.listServices(remoteDevice));
			System.out.printf("}%n%n");
		}
	}

	private void printServices(List<ServiceRecord> services) {
		for (ServiceRecord service : services) {
			System.out.printf("%n\t\"%s\"%n", Bluetooth.getServiceName(service));
		}
	}
}
