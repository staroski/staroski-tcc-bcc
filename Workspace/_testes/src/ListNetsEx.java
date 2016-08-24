import static java.lang.System.out;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

public class ListNetsEx {

	private static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
		out.printf("Display name: %s\n", netint.getDisplayName());
		out.printf("Name: %s\n", netint.getName());
		Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
		for (InetAddress inetAddress : Collections.list(inetAddresses)) {
			out.printf("InetAddress: %s\n", inetAddress);
		}
		out.printf("Up? %s\n", netint.isUp());
		out.printf("Loopback? %s\n", netint.isLoopback());
		out.printf("PointToPoint? %s\n", netint.isPointToPoint());
		out.printf("Supports multicast? %s\n", netint.supportsMulticast());
		out.printf("Virtual? %s\n", netint.isVirtual());
		out.printf("Hardware address: %s\n", Arrays.toString(netint.getHardwareAddress()));
		out.printf("MTU: %s\n", netint.getMTU());
		out.printf("\n");
	}

	private static boolean hasHardwareAddress(NetworkInterface netint) throws IOException {
		return netint.getHardwareAddress() != null;
	}

	private static boolean isBluetooth(NetworkInterface netint) {
		String bluetooth = "bluetooth";
		String displayName = netint.getDisplayName().toLowerCase();
		String name = netint.getName().toLowerCase();
		return displayName.contains(bluetooth) || name.contains(bluetooth);
	}

	public static void main(String args[]) throws IOException {
		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
		for (NetworkInterface netint : Collections.list(nets)) {
			if (!isBluetooth(netint)) {
				continue;
			}
			if (!hasHardwareAddress(netint)) {
				continue;
			}
			displayInterfaceInformation(netint);
			tryToConnect(netint);
		}
	}

	private static void tryToConnect(NetworkInterface netint) {
		try {
			InterfaceAddress interfaceAddress = netint.getInterfaceAddresses().iterator().next();

			InetAddress address = interfaceAddress.getAddress();

			// InetAddress address = netint.getInetAddresses().nextElement();

			DatagramSocket client = new DatagramSocket();
			client.setBroadcast(true);
			byte[] data = "010D\r".getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, 0);
			client.send(packet);

			client.receive(packet);
			data = packet.getData();
			String response = new String(data);
			System.out.println("Server responded: " + response);
			client.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	// private static void tryToConnect(NetworkInterface netint) {
	// try {
	// InetSocketAddress address = new
	// InetSocketAddress(netint.getInetAddresses().nextElement(), 0);
	// DatagramChannel client = DatagramChannel.open();
	// client.bind(null);
	// ByteBuffer buffer = ByteBuffer.wrap("010D\r".getBytes());
	// client.send(buffer, address);
	// buffer.clear();
	// client.receive(buffer);
	// buffer.flip();
	// int limits = buffer.limit();
	// byte bytes[] = new byte[limits];
	// buffer.get(bytes, 0, limits);
	// String response = new String(bytes);
	// System.out.println("Server responded: " + response);
	// client.close();
	// } catch (Throwable t) {
	// t.printStackTrace();
	// }
	// }

	// private static void tryToConnect(NetworkInterface netint) {
	//
	// try {
	// DatagramChannel client = DatagramChannel.open();
	// client.bind(null);
	// String msg = "010D\r";
	// ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
	// InetSocketAddress serverAddress = new
	// InetSocketAddress(netint.getInetAddresses().nextElement(), 2424);
	// client.send(buffer, serverAddress);
	// buffer.clear();
	// client.receive(buffer);
	// buffer.flip();
	// int limits = buffer.limit();
	// byte bytes[] = new byte[limits];
	// buffer.get(bytes, 0, limits);
	// String response = new String(bytes);
	// System.out.println("Server responded: " + response);
	// } catch (Throwable t) {
	// t.printStackTrace();
	// }
	// }
}