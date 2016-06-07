/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2008 Michael Lifshits
 *  Copyright (C) 2008 Vlad Skarzhevskyy
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 *  @author vlads
 *  @version $Id: DeviceManagerServiceImpl.java 2600 2008-12-16 22:22:43Z skarzhevskyy $
 */
package com.intel.bluetooth.emu;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.ServiceRegistrationException;

import com.intel.bluetooth.DebugLog;
import com.intel.bluetooth.RemoteDeviceHelper;

public class DeviceManagerServiceImpl implements DeviceManagerService {

	public static final int MAJOR_COMPUTER = 0x0100;

	static final EmulatorConfiguration configuration;

	private static Map<Long, Device> devices = new Hashtable<Long, Device>();

	static {
		configuration = new EmulatorConfiguration();
		configuration.loadConfigFile();
	}

	public DeviceManagerServiceImpl() {
	}

	public void shutdown() {
		synchronized (devices) {
			for (Iterator<Device> iterator = devices.values().iterator(); iterator.hasNext();) {
				Device device = iterator.next();
				device.release();
			}
			devices.clear();
		}
	}

	public DeviceDescriptor createNewDevice(String deviceID, String deviceAddress) throws BluetoothStateException {
		synchronized (devices) {
			long address = getNextAvailableBTAddress(deviceID, deviceAddress);

			String name = configuration.getProperty(address, EmulatorConfiguration.deviceName);
			if (name == null) {
				name = configuration.getDeviceNamePrefix() + RemoteDeviceHelper.getBluetoothAddress(address);
			}
			int deviceClass = MAJOR_COMPUTER;
			String cod = configuration.getProperty(address, EmulatorConfiguration.deviceClass);
			if (cod != null) {
				deviceClass = EmulatorConfiguration.valueToInt(cod);
			}
			DeviceDescriptor descriptor = new DeviceDescriptor(address, name, deviceClass);

			if (!configuration.isDeviceDiscoverable()) {
				descriptor.setDiscoverableMode(DiscoveryAgent.NOT_DISCOVERABLE);
			}

			devices.put(new Long(address), new Device(descriptor));
			return descriptor;
		}
	}

	public EmulatorConfiguration getEmulatorConfiguration(long localAddress) {
		return configuration.clone(localAddress);
	}

	public void releaseDevice(long address) {
		Device device;
		synchronized (devices) {
			device = (Device) devices.remove(new Long(address));
		}
		if (device != null) {
			device.release();
		}
	}

	public DeviceCommand pollCommand(long address) {
		Device device = getDevice(address);
		if (device == null) {
			throw new RuntimeException("No such device " + RemoteDeviceHelper.getBluetoothAddress(address));
		}
		return device.pollCommand();
	}

	static Device getDevice(long address) {
		Device d = ((Device) devices.get(new Long(address)));
		if ((d != null) && (!d.isAlive())) {
			synchronized (devices) {
				devices.remove(new Long(address));
			}
			d.died();
			return null;
		}
		return d;
	}

	private Device getActiveDevice(long address) {
		Device d = getDevice(address);
		if ((d != null) && (!d.getDescriptor().isPoweredOn())) {
			return null;
		} else {
			return d;
		}
	}

	public DeviceDescriptor getDeviceDescriptor(long address) {
		Device device = getDevice(address);
		if (device == null) {
			throw new RuntimeException("No such device " + RemoteDeviceHelper.getBluetoothAddress(address));
		}
		return device.getDescriptor();
	}

	private DeviceSDP getDeviceSDP(long address) {
		Device device = getActiveDevice(address);
		if (device == null) {
			return null;
		}
		return device.getDeviceSDP(false);
	}

	public static List<MonitorDevice> getMonitorDevices() {
		Vector<MonitorDevice> monitorDevices = new Vector<MonitorDevice>();
		synchronized (devices) {
			for (Iterator<Device> iterator = devices.values().iterator(); iterator.hasNext();) {
				Device device = iterator.next();
				if (!device.isAlive()) {
					iterator.remove();
					device.died();
					continue;
				}
				isDiscoverable(device.getDescriptor());
				monitorDevices.add(new MonitorDevice(device));
			}
		}
		return monitorDevices;
	}

	public DeviceDescriptor[] getDiscoveredDevices(long address) {
		Vector<DeviceDescriptor> discoveredDevice = new Vector<DeviceDescriptor>();
		synchronized (devices) {
			for (Iterator<Device> iterator = devices.values().iterator(); iterator.hasNext();) {
				Device device = iterator.next();
				if (!device.isAlive()) {
					iterator.remove();
					device.died();
					continue;
				}
				if (device.getDescriptor().getAddress() == address) {
					continue;
				}
				if (isDiscoverable(device.getDescriptor())) {
					discoveredDevice.addElement(device.getDescriptor());
				}
			}
		}
		return (DeviceDescriptor[]) discoveredDevice.toArray(new DeviceDescriptor[discoveredDevice.size()]);
	}

	private static boolean isDiscoverable(DeviceDescriptor device) {
		if (!device.isPoweredOn()) {
			return false;
		}
		int discoverableMode = device.getDiscoverableMode();
		switch (discoverableMode) {
		case DiscoveryAgent.NOT_DISCOVERABLE:
			return false;
		case DiscoveryAgent.GIAC:
			return true;
		case DiscoveryAgent.LIAC:
			if (device.getLimitedDiscoverableStart() + configuration.getDurationLIAC() * 1000 * 60 < System
					.currentTimeMillis()) {
				DebugLog.debug(RemoteDeviceHelper.getBluetoothAddress(device.getAddress())
						+ " LIAC -> NOT_DISCOVERABLE");
				device.setDiscoverableMode(DiscoveryAgent.NOT_DISCOVERABLE);
				return false;
			} else {
				return true;
			}
		default:
			// No idea what are other modes are.
			return true;
		}
	}

	public boolean isLocalDevicePowerOn(long localAddress) {
		return getDeviceDescriptor(localAddress).isPoweredOn();
	}

	public void setLocalDevicePower(long localAddress, boolean on) {
		Device device = getDevice(localAddress);
		if (device == null) {
			throw new RuntimeException("No such device " + RemoteDeviceHelper.getBluetoothAddress(localAddress));
		}
		device.setDevicePower(on);
	}

	public int getLocalDeviceDiscoverable(long localAddress) {
		DeviceDescriptor dd = getDeviceDescriptor(localAddress);
		if (!dd.isPoweredOn()) {
			return DiscoveryAgent.NOT_DISCOVERABLE;
		}
		// Update mode if it was LIAC
		isDiscoverable(dd);
		return dd.getDiscoverableMode();
	}

	public boolean setLocalDeviceDiscoverable(long localAddress, int mode) throws BluetoothStateException {
		DeviceDescriptor dd = getDeviceDescriptor(localAddress);
		if (!dd.isPoweredOn()) {
			throw new BluetoothStateException("Device power is off");
		}
		DebugLog.debug(RemoteDeviceHelper.getBluetoothAddress(localAddress) + " setDiscoverableMode", EmulatorUtils
				.discoverableModeString(mode));
		dd.setDiscoverableMode(mode);
		return true;
	}

	public void setLocalDeviceServiceClasses(long localAddress, int classOfDevice) {
		DebugLog.debug(RemoteDeviceHelper.getBluetoothAddress(localAddress) + " setServiceClasses ", classOfDevice);
		getDeviceDescriptor(localAddress).setDeviceClass(classOfDevice);
	}

	public String getRemoteDeviceFriendlyName(long address) throws IOException {
		DeviceDescriptor dd = getDeviceDescriptor(address);
		if (!dd.isPoweredOn()) {
			throw new IOException("Remote device power is off");
		}
		return dd.getName();
	}

	private long getNextAvailableBTAddress(String deviceID, String deviceAddress) throws BluetoothStateException {
		if (deviceID != null) {
			long id = configuration.getFirstDeviceAddress() + Long.parseLong(deviceID);
			if (getDevice(id) != null) {
				throw new BluetoothStateException("Device already reserved "
						+ RemoteDeviceHelper.getBluetoothAddress(id));
			}
			return id;
		} else if (deviceAddress != null) {
			long address = RemoteDeviceHelper.getAddress(deviceAddress);
			if (getDevice(address) != null) {
				throw new BluetoothStateException("Device already reserved "
						+ RemoteDeviceHelper.getBluetoothAddress(address));
			}
			return address;
		} else {
			return EmulatorUtils.getNextAvailable(devices.keySet(), configuration.getFirstDeviceAddress(), 1);
		}

	}

	public void updateServiceRecord(long address, long handle, ServicesDescriptor sdpData)
			throws ServiceRegistrationException {
		Device device = getActiveDevice(address);
		if (device == null) {
			throw new ServiceRegistrationException("No such device " + RemoteDeviceHelper.getBluetoothAddress(address));
		}
		DeviceSDP ds = device.getDeviceSDP(true);
		ds.updateServiceRecord(handle, sdpData);
	}

	public void removeServiceRecord(long address, long handle) throws IOException {
		DeviceSDP ds = getDeviceSDP(address);
		if (ds != null) {
			ds.removeServiceRecord(handle);
		}
	}

	public long[] searchServices(long address, String[] uuidSet) {
		if (getActiveDevice(address) == null) {
			return null;
		}
		DeviceSDP ds = getDeviceSDP(address);
		if (ds == null) {
			return new long[0];
		}
		return ds.searchServices(uuidSet);
	}

	public byte[] getServicesRecordBinary(long address, long handle) throws IOException {
		DeviceSDP ds = getDeviceSDP(address);
		if (ds == null) {
			throw new IOException("No such device " + RemoteDeviceHelper.getBluetoothAddress(address));
		}
		ServicesDescriptor sd = ds.getServicesDescriptor(handle);
		if (sd == null) {
			throw new IOException("No such service");
		}
		return sd.getSdpBinary();
	}

	public void rfOpenService(long localAddress, int channel) throws IOException {
		openService(localAddress, ServiceListener.rfPrefix(channel));
	}

	public long rfAccept(long localAddress, int channel, boolean authenticate, boolean encrypt) throws IOException {
		return accept(localAddress, ServiceListener.rfPrefix(channel), authenticate, encrypt, 0);
	}

	public long rfConnect(long localAddress, long remoteAddress, int channel, boolean authenticate, boolean encrypt,
			int timeout) throws IOException {
		return connect(localAddress, remoteAddress, ServiceListener.rfPrefix(channel), authenticate, encrypt, 0,
				timeout);
	}

	public void rfCloseService(long address, int pcm) {
		closeService(address, ServiceListener.rfPrefix(pcm));
	}

	public void l2OpenService(long localAddress, int pcm) throws IOException {
		openService(localAddress, ServiceListener.l2Prefix(pcm));
	}

	public long l2Accept(long localAddress, int channel, boolean authenticate, boolean encrypt, int receiveMTU)
			throws IOException {
		return accept(localAddress, ServiceListener.l2Prefix(channel), authenticate, encrypt, receiveMTU);
	}

	public long l2Connect(long localAddress, long remoteAddress, int channel, boolean authenticate, boolean encrypt,
			int receiveMTU, int timeout) throws IOException {
		return connect(localAddress, remoteAddress, ServiceListener.l2Prefix(channel), authenticate, encrypt,
				receiveMTU, timeout);
	}

	public void l2CloseService(long address, int channel) {
		closeService(address, ServiceListener.l2Prefix(channel));
	}

	private long accept(long localAddress, String channelID, boolean authenticate, boolean encrypt, int receiveMTU)
			throws IOException {
		Device device;
		if ((device = getActiveDevice(localAddress)) == null) {
			throw new IOException("No such device " + RemoteDeviceHelper.getBluetoothAddress(localAddress));
		}
		ServiceListener sl = device.createServiceListener(channelID);
		return sl.accept(device, authenticate, encrypt, receiveMTU);
	}

	private long connect(long localAddress, long remoteAddress, String portID, boolean authenticate, boolean encrypt,
			int receiveMTU, int timeout) throws IOException {
		Device remoteDevice = getActiveDevice(remoteAddress);
		if (remoteDevice == null) {
			throw new BluetoothConnectionException(BluetoothConnectionException.FAILED_NOINFO, "No such device "
					+ RemoteDeviceHelper.getBluetoothAddress(remoteAddress));
		}
		Device localDevice = getActiveDevice(localAddress);
		if (localDevice == null) {
			throw new BluetoothConnectionException(BluetoothConnectionException.FAILED_NOINFO, "No such device "
					+ RemoteDeviceHelper.getBluetoothAddress(localAddress));
		}
		if (localDevice.getConnectionBuffer(remoteAddress, portID) != null) {
			throw new BluetoothConnectionException(BluetoothConnectionException.FAILED_NOINFO,
					"Already connected to the same port " + portID + " on "
							+ RemoteDeviceHelper.getBluetoothAddress(remoteAddress));
		}
		ServiceListener sl = remoteDevice.connectService(portID, timeout);
		if (sl == null) {
			throw new BluetoothConnectionException(BluetoothConnectionException.UNKNOWN_PSM, "No such service "
					+ portID);
		}
		return sl.connect(localDevice, authenticate, encrypt, receiveMTU, timeout);
	}

	public void connectionAccepted(long localAddress, long connectionId) throws IOException {
		Device device;
		if ((device = getActiveDevice(localAddress)) == null) {
			throw new IOException("No such device " + RemoteDeviceHelper.getBluetoothAddress(localAddress));
		}
		ConnectionBuffer c = device.getConnectionBuffer(connectionId);
		if (c == null) {
			throw new IOException("No such connection " + connectionId);
		}
		c.accepted();
	}

	private void openService(long address, String channelID) throws IOException {
		Device device = getActiveDevice(address);
		if (device == null) {
			throw new IOException("No such device " + RemoteDeviceHelper.getBluetoothAddress(address));
		}
		device.openService(channelID);
	}

	private void closeService(long address, String channelID) {
		Device device = getDevice(address);
		if (device == null) {
			return;
		}
		device.closeService(channelID);
	}

	private ConnectionBuffer getConnectionBuffer(long localAddress, long connectionId) throws IOException {
		Device localDevice = getActiveDevice(localAddress);
		if (localDevice == null) {
			throw new IOException("No such device " + RemoteDeviceHelper.getBluetoothAddress(localAddress));
		}
		ConnectionBuffer c = localDevice.getConnectionBuffer(connectionId);
		if (c == null) {
			throw new IOException("No such connection " + connectionId);
		}
		return c;
	}

	public long getRemoteAddress(long localAddress, long connectionId) throws IOException {
		return getConnectionBuffer(localAddress, connectionId).getRemoteAddress();
	}

	public void rfWrite(long localAddress, long connectionId, byte[] b) throws IOException {
		((ConnectionBufferRFCOMM) getConnectionBuffer(localAddress, connectionId)).rfWrite(b);
	}

	public void rfFlush(long localAddress, long connectionId) throws IOException {
		((ConnectionBufferRFCOMM) getConnectionBuffer(localAddress, connectionId)).rfFlush();
	}

	public int rfAvailable(long localAddress, long connectionId) throws IOException {
		return ((ConnectionBufferRFCOMM) getConnectionBuffer(localAddress, connectionId)).rfAvailable();
	}

	public byte[] rfRead(long localAddress, long connectionId, int len) throws IOException {
		return ((ConnectionBufferRFCOMM) getConnectionBuffer(localAddress, connectionId)).rfRead(len);
	}

	public void closeConnection(long localAddress, long connectionId) throws IOException {
		Device localDevice = getDevice(localAddress);
		if (localDevice == null) {
			throw new IOException("No such device " + RemoteDeviceHelper.getBluetoothAddress(localAddress));
		}
		localDevice.closeConnection(connectionId);
	}

	public int getSecurityOpt(long localAddress, long connectionId, int expected) throws IOException {
		return ((ConnectionBuffer) getConnectionBuffer(localAddress, connectionId)).getSecurityOpt(expected);
	}

	public boolean encrypt(long localAddress, long connectionId, long remoteAddress, boolean on) throws IOException {
		return ((ConnectionBuffer) getConnectionBuffer(localAddress, connectionId)).encrypt(remoteAddress, on);
	}

	public int l2RemoteDeviceReceiveMTU(long localAddress, long connectionId) throws IOException {
		return ((ConnectionBufferL2CAP) getConnectionBuffer(localAddress, connectionId)).getRemoteReceiveMTU();
	}

	public boolean l2Ready(long localAddress, long connectionId) throws IOException {
		return ((ConnectionBufferL2CAP) getConnectionBuffer(localAddress, connectionId)).ready();
	}

	public byte[] l2Receive(long localAddress, long connectionId, int len) throws IOException {
		return ((ConnectionBufferL2CAP) getConnectionBuffer(localAddress, connectionId)).receive(len);
	}

	public void l2Send(long localAddress, long connectionId, byte[] data) throws IOException {
		((ConnectionBufferL2CAP) getConnectionBuffer(localAddress, connectionId)).send(data);
	}
}
