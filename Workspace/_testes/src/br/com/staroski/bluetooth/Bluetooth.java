package br.com.staroski.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

public final class Bluetooth {

	public static final Bluetooth get;

	static {
		try {
			get = new Bluetooth();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError(e);
		}
	}

	private final DiscoveryAgent discoveryAgent;
	private List<ServiceRecord> services;

	private Bluetooth() throws IOException {
		discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
	}

	public RemoteDevice device(final DeviceFilter... filters) throws IOException {
		List<RemoteDevice> devices = devices(filters);
		return devices.isEmpty() ? null : devices.get(0);
	}

	public List<RemoteDevice> devices(final DeviceFilter... filters) throws IOException {
		try {
			final List<RemoteDevice> devicesDiscovered = new ArrayList<>();
			final Object LOCK = new Object();
			synchronized (LOCK) {
				boolean started = discoveryAgent.startInquiry(DiscoveryAgent.GIAC, new DiscoveryAdapter() {

					public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
						for (DeviceFilter filter : filters) {
							if (!filter.accept(btDevice)) {
								return;
							}
						}
						devicesDiscovered.add(btDevice);
					}

					public void inquiryCompleted(int discType) {
						synchronized (LOCK) {
							LOCK.notifyAll();
						}
					}
				});
				if (started) {
					LOCK.wait();
				}
			}
			return devicesDiscovered;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public ServiceRecord service(ServiceCriteria criteria) throws IOException {
		services = services(criteria);
		return services.isEmpty() ? null : services.get(0);
	}

	public List<ServiceRecord> services(ServiceCriteria criteria) throws IOException {
		try {
			final List<ServiceRecord> services = new ArrayList<>();
			final Object LOCK = new Object();
			synchronized (LOCK) {
				int transactionID = discoveryAgent.searchServices(criteria.attributes(), criteria.uuids(),
						criteria.device(), new DiscoveryAdapter() {

							public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
								services.addAll(Arrays.asList(servRecord));
							}

							public void serviceSearchCompleted(int transID, int respCode) {
								synchronized (LOCK) {
									LOCK.notifyAll();
								}
							}
						});
				if (transactionID > 0) {
					LOCK.wait();
				}
				return services;
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
