import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class TestBluetoothV2 {

	public static void main(String[] args) {
		try {
			new TestBluetoothV2().execute();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	private void execute() throws IOException {
		List<NetworkInterface> bluetoothInterfaces = getBluetoothInterfaces();
		for (NetworkInterface networkInterface : bluetoothInterfaces) {
			printNetworkInterface(networkInterface);
		}
	}

	// private void connectToInetAddress(InetAddress address) throws IOException
	// {
	// System.out.println(address.getCanonicalHostName());
	//
	// int p = 21; // ftp
	// int p = 22; // ssh / sftp
	// int p = 23; // telnet
	// int p = 25; // smtp
	// int p = 80; // http
	// int p = 110; // pop3
	// int p = 143; // imap
	//
	// Socket socket = new Socket(address, p);
	// System.err.println("out: " + socket.getInputStream());
	// System.err.println("out: " + socket.getOutputStream());
	// socket.close();
	// // URLConnection connection = url.openConnection();
	// // InputStream in = connection.getInputStream();
	// // OutputStream out = connection.getOutputStream();
	// // System.out.println("in: " + in);
	// // System.out.println("out: " + out);
	// }

	private void printNetworkInterface(NetworkInterface netint) throws IOException {
		System.out.printf("Display name: %s\n", netint.getDisplayName());
		System.out.printf("Name: %s\n", netint.getName());
		System.out.printf("Up? %s\n", netint.isUp());
		System.out.printf("Loopback? %s\n", netint.isLoopback());
		System.out.printf("PointToPoint? %s\n", netint.isPointToPoint());
		System.out.printf("Supports multicast? %s\n", netint.supportsMulticast());
		System.out.printf("Virtual? %s\n", netint.isVirtual());
		System.out.printf("Hardware address: %s\n", Arrays.toString(netint.getHardwareAddress()));
		System.out.printf("MTU: %s\n", netint.getMTU());
		Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
		for (InetAddress inetAddress : Collections.list(inetAddresses)) {
			System.out.printf("InetAddress: %s\n", inetAddress);
		}
		List<InterfaceAddress> interfaceAddresses = netint.getInterfaceAddresses();
		for (InterfaceAddress subitf:interfaceAddresses){
			System.out.printf("Sub InterfaceAddress: %s\n", subitf);
		}
		System.out.printf("\n");
	}

	public List<NetworkInterface> getBluetoothInterfaces() throws IOException {
		List<NetworkInterface> bluetoothInterfaces = new ArrayList<>();
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		final String bluetooth = "bluetooth";
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			if (networkInterface.getDisplayName().toLowerCase().contains(bluetooth) //
					|| networkInterface.getName().toLowerCase().contains(bluetooth)) {
				if (networkInterface.getInetAddresses().hasMoreElements()) {
					bluetoothInterfaces.add(networkInterface);
				}
			}
		}
		return bluetoothInterfaces;
	}
}
