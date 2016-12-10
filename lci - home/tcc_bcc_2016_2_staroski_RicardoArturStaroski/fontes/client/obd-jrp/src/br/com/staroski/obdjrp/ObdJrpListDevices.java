package br.com.staroski.obdjrp;

import java.io.IOException;
import java.util.List;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import br.com.staroski.obdjrp.bluetooth.Bluetooth;

public final class ObdJrpListDevices extends ObdJrpApp {	

	public static void main(String[] args) {
		try {
			ObdJrpListDevices program = new ObdJrpListDevices();
			program.execute();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
	}

	private ObdJrpListDevices() throws IOException {
		super("list-devices");
	}

	private void execute() throws IOException {
		List<RemoteDevice> devices = Bluetooth.listDevices();
		for (RemoteDevice device : devices) {
			String address = device.getBluetoothAddress();
			String name = device.getFriendlyName(false);
			System.out.printf("device \"%s\" - \"%s\" {", address, name);
			printServices(device);
			System.out.printf("}%n%n");
		}
	}

	private void printServices(RemoteDevice device) throws IOException {
		List<ServiceRecord> services = Bluetooth.listServices(device);
		for (ServiceRecord service : services) {
			System.out.printf("%n\t\"%s\"%n", Bluetooth.getServiceName(service));
		}
	}
}
