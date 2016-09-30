/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2009 Vlad Skarzhevskyy
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
 *  @version $Id: BlueZAPIV3.java 2988 2009-04-22 22:15:55Z skarzhevskyy $
 */
package org.bluez.v3;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.bluetooth.DiscoveryAgent;

import org.bluez.BlueZAPI;
import org.bluez.Error.Canceled;
import org.bluez.Error.Failed;
import org.bluez.Error.InvalidArguments;
import org.bluez.Error.NoSuchAdapter;
import org.bluez.Error.NotReady;
import org.bluez.Error.Rejected;
import org.freedesktop.DBus;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;

import com.intel.bluetooth.BlueCoveImpl;
import com.intel.bluetooth.BluetoothConsts;
import com.intel.bluetooth.DebugLog;

/**
 * 
 * Access BlueZ v3 over D-Bus
 * 
 */
public class BlueZAPIV3 implements BlueZAPI {

    private DBusConnection dbusConn;

    private Manager dbusManager;

    private Adapter adapter;

    private Path adapterPath;

    private long lastDeviceDiscoveryTime = 0;

    public BlueZAPIV3(DBusConnection dbusConn, Manager dbusManager) {
        this.dbusConn = dbusConn;
        this.dbusManager = dbusManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#findAdapter(java.lang.String)
     */
    public Path findAdapter(String pattern) throws InvalidArguments {
        String path;
        try {
            path = dbusManager.FindAdapter(pattern);
        } catch (NoSuchAdapter e) {
            return null;
        }
        if (path == null) {
            return null;
        } else {
            return new Path(path);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#defaultAdapter()
     */
    public Path defaultAdapter() throws InvalidArguments {
        String path;
        try {
            path = dbusManager.DefaultAdapter();
        } catch (NoSuchAdapter e) {
            return null;
        }
        if (path == null) {
            return null;
        } else {
            return new Path(path);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#getAdapter(int)
     */
    public Path getAdapter(int number) {
        String[] adapters = dbusManager.ListAdapters();
        if (adapters == null) {
            throw null;
        }
        if ((number < 0) || (number >= adapters.length)) {
            throw null;
        }
        return new Path(String.valueOf(adapters[number]));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#listAdapters()
     */
    public List<String> listAdapters() {
        List<String> v = new Vector<String>();
        String[] adapters = dbusManager.ListAdapters();
        if (adapters != null) {
            for (int i = 0; i < adapters.length; i++) {
                String adapterId = String.valueOf(adapters[i]);
                final String bluezPath = "/org/bluez/";
                if (adapterId.startsWith(bluezPath)) {
                    adapterId = adapterId.substring(bluezPath.length());
                }
                v.add(adapterId);
            }
        }
        return v;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#selectAdapter(org.freedesktop.dbus.Path)
     */
    public void selectAdapter(Path adapterPath) throws DBusException {
        DebugLog.debug("selectAdapter", adapterPath.getPath());
        adapter = dbusConn.getRemoteObject("org.bluez", adapterPath.getPath(), Adapter.class);
        this.adapterPath = adapterPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#getAdapterAddress()
     */
    public String getAdapterAddress() {
        return adapter.GetAddress();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#getAdapterID()
     */
    public String getAdapterID() {
        final String bluezPath = "/org/bluez/";
        if (adapterPath.getPath().startsWith(bluezPath)) {
            return adapterPath.getPath().substring(bluezPath.length());
        } else {
            return adapterPath.getPath();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#getAdapterDeviceClass()
     */
    public int getAdapterDeviceClass() {
        int record = 0;
        String major = adapter.GetMajorClass();

        if ("computer".equals(major)) {
            record |= BluetoothConsts.DeviceClassConsts.MAJOR_COMPUTER;
        } else {
            DebugLog.debug("Unknown MajorClass", major);
        }

        String minor = adapter.GetMinorClass();
        if (minor.equals("uncategorized")) {
            record |= BluetoothConsts.DeviceClassConsts.COMPUTER_MINOR_UNCLASSIFIED;
        } else if (minor.equals("desktop")) {
            record |= BluetoothConsts.DeviceClassConsts.COMPUTER_MINOR_DESKTOP;
        } else if (minor.equals("server")) {
            record |= BluetoothConsts.DeviceClassConsts.COMPUTER_MINOR_SERVER;
        } else if (minor.equals("laptop")) {
            record |= BluetoothConsts.DeviceClassConsts.COMPUTER_MINOR_LAPTOP;
        } else if (minor.equals("handheld")) {
            record |= BluetoothConsts.DeviceClassConsts.COMPUTER_MINOR_HANDHELD;
        } else if (minor.equals("palm")) {
            record |= BluetoothConsts.DeviceClassConsts.COMPUTER_MINOR_PALM;
        } else if (minor.equals("wearable")) {
            record |= BluetoothConsts.DeviceClassConsts.COMPUTER_MINOR_WEARABLE;
        } else {
            DebugLog.debug("Unknown MinorClass", minor);
            record |= BluetoothConsts.DeviceClassConsts.COMPUTER_MINOR_UNCLASSIFIED;
        }

        String[] srvc = adapter.GetServiceClasses();
        if (srvc != null) {
            for (int s = 0; s < srvc.length; s++) {
                String serviceClass = srvc[s];
                if (serviceClass.equals("positioning")) {
                    record |= BluetoothConsts.DeviceClassConsts.POSITIONING_SERVICE;
                } else if (serviceClass.equals("networking")) {
                    record |= BluetoothConsts.DeviceClassConsts.NETWORKING_SERVICE;
                } else if (serviceClass.equals("rendering")) {
                    record |= BluetoothConsts.DeviceClassConsts.RENDERING_SERVICE;
                } else if (serviceClass.equals("capturing")) {
                    record |= BluetoothConsts.DeviceClassConsts.CAPTURING_SERVICE;
                } else if (serviceClass.equals("object transfer")) {
                    record |= BluetoothConsts.DeviceClassConsts.OBJECT_TRANSFER_SERVICE;
                } else if (serviceClass.equals("audio")) {
                    record |= BluetoothConsts.DeviceClassConsts.AUDIO_SERVICE;
                } else if (serviceClass.equals("telephony")) {
                    record |= BluetoothConsts.DeviceClassConsts.TELEPHONY_SERVICE;
                } else if (serviceClass.equals("information")) {
                    record |= BluetoothConsts.DeviceClassConsts.INFORMATION_SERVICE;
                } else {
                    DebugLog.debug("Unknown ServiceClasses", serviceClass);
                }
            }
        }

        return record;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#getAdapterName()
     */
    public String getAdapterName() {
        try {
            return adapter.GetName();
        } catch (NotReady e) {
            return null;
        } catch (Failed e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#isAdapterDiscoverable()
     */
    public boolean isAdapterDiscoverable() {
        return adapter.IsDiscoverable();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#getAdapterDiscoverableTimeout()
     */
    public int getAdapterDiscoverableTimeout() {
        return adapter.GetDiscoverableTimeout().intValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#setAdapterDiscoverable(int)
     */
    public boolean setAdapterDiscoverable(int mode) throws DBusException {
        String modeStr;
        switch (mode) {
        case DiscoveryAgent.NOT_DISCOVERABLE:
            modeStr = "connectable";
            break;
        case DiscoveryAgent.GIAC:
            modeStr = "discoverable";
            break;
        case DiscoveryAgent.LIAC:
            modeStr = "limited";
            break;
        default:
            if ((0x9E8B00 <= mode) && (mode <= 0x9E8B3F)) {
                // system does not support the access mode specified
                return false;
            }
            throw new IllegalArgumentException("Invalid discoverable mode");
        }
        adapter.SetMode(modeStr);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#getAdapterManufacturer()
     */
    public String getAdapterManufacturer() {
        return adapter.GetManufacturer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#getAdapterRevision()
     */
    public String getAdapterRevision() {
        return adapter.GetRevision();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#getAdapterVersion()
     */
    public String getAdapterVersion() {
        return adapter.GetVersion();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#isAdapterPowerOn()
     */
    public boolean isAdapterPowerOn() {
        return !"off".equals(adapter.GetMode());
    }

    private <T extends DBusSignal> void quietRemoveSigHandler(Class<T> type, DBusSigHandler<T> handler) {
        try {
            dbusConn.removeSigHandler(type, handler);
        } catch (DBusException ignore) {
        }
    }

    private boolean hasBonding(String deviceAddress) {
        try {
            return adapter.HasBonding(deviceAddress);
        } catch (Throwable e) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#deviceInquiry(org.bluez.BlueZAPI.DeviceInquiryListener )
     */
    public void deviceInquiry(final DeviceInquiryListener listener) throws DBusException, InterruptedException {

        final Object discoveryCompletedEvent = new Object();

        DBusSigHandler<Adapter.DiscoveryCompleted> discoveryCompleted = new DBusSigHandler<Adapter.DiscoveryCompleted>() {
            public void handle(Adapter.DiscoveryCompleted s) {
                DebugLog.debug("discoveryCompleted.handle()");
                synchronized (discoveryCompletedEvent) {
                    discoveryCompletedEvent.notifyAll();
                }
            }
        };

        DBusSigHandler<Adapter.DiscoveryStarted> discoveryStarted = new DBusSigHandler<Adapter.DiscoveryStarted>() {
            public void handle(Adapter.DiscoveryStarted s) {
                DebugLog.debug("device discovery procedure has been started.");
                //TODO
            }
        };

        DBusSigHandler<Adapter.RemoteDeviceFound> remoteDeviceFound = new DBusSigHandler<Adapter.RemoteDeviceFound>() {
            public void handle(Adapter.RemoteDeviceFound s) {
                listener.deviceDiscovered(s.getDeviceAddress(), null, s.getDeviceClass().intValue(), hasBonding(s.getDeviceAddress()));
            }
        };

        DBusSigHandler<Adapter.RemoteNameUpdated> remoteNameUpdated = new DBusSigHandler<Adapter.RemoteNameUpdated>() {
            public void handle(Adapter.RemoteNameUpdated s) {
                listener.deviceDiscovered(s.getDeviceAddress(), s.getDeviceName(), -1, false);
            }
        };

        DBusSigHandler<Adapter.RemoteClassUpdated> remoteClassUpdated = new DBusSigHandler<Adapter.RemoteClassUpdated>() {
            public void handle(Adapter.RemoteClassUpdated s) {
                listener.deviceDiscovered(s.getDeviceAddress(), null, s.getDeviceClass().intValue(), hasBonding(s.getDeviceAddress()));
            }
        };

        try {
            dbusConn.addSigHandler(Adapter.DiscoveryCompleted.class, discoveryCompleted);
            dbusConn.addSigHandler(Adapter.DiscoveryStarted.class, discoveryStarted);
            dbusConn.addSigHandler(Adapter.RemoteDeviceFound.class, remoteDeviceFound);
            dbusConn.addSigHandler(Adapter.RemoteNameUpdated.class, remoteNameUpdated);
            dbusConn.addSigHandler(Adapter.RemoteClassUpdated.class, remoteClassUpdated);

            // Inquiries are throttled if they are called too quickly in succession.
            // e.g. JSR-82 TCK
            long sinceDiscoveryLast = System.currentTimeMillis() - lastDeviceDiscoveryTime;
            long acceptableInterval = 5 * 1000;
            if (sinceDiscoveryLast < acceptableInterval) {
                Thread.sleep(acceptableInterval - sinceDiscoveryLast);
            }

            synchronized (discoveryCompletedEvent) {
                adapter.DiscoverDevices();
                listener.deviceInquiryStarted();
                DebugLog.debug("wait for device inquiry to complete...");
                discoveryCompletedEvent.wait();
                //adapter.CancelDiscovery();
            }

        } finally {
            quietRemoveSigHandler(Adapter.RemoteClassUpdated.class, remoteClassUpdated);
            quietRemoveSigHandler(Adapter.RemoteNameUpdated.class, remoteNameUpdated);
            quietRemoveSigHandler(Adapter.RemoteDeviceFound.class, remoteDeviceFound);
            quietRemoveSigHandler(Adapter.DiscoveryStarted.class, discoveryStarted);
            quietRemoveSigHandler(Adapter.DiscoveryCompleted.class, discoveryCompleted);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#deviceInquiryCancel()
     */
    public void deviceInquiryCancel() throws DBusException {
        adapter.CancelDiscovery();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#getRemoteDeviceFriendlyName(java.lang.String)
     */
    public String getRemoteDeviceFriendlyName(final String deviceAddress) throws DBusException, IOException {
        final Object discoveryCompletedEvent = new Object();
        final Vector<String> namesFound = new Vector<String>();

        DBusSigHandler<Adapter.DiscoveryCompleted> discoveryCompleted = new DBusSigHandler<Adapter.DiscoveryCompleted>() {
            public void handle(Adapter.DiscoveryCompleted s) {
                DebugLog.debug("discoveryCompleted.handle()");
                synchronized (discoveryCompletedEvent) {
                    discoveryCompletedEvent.notifyAll();
                }
            }
        };

        DBusSigHandler<Adapter.RemoteNameUpdated> remoteNameUpdated = new DBusSigHandler<Adapter.RemoteNameUpdated>() {
            public void handle(Adapter.RemoteNameUpdated s) {
                if (deviceAddress.equals(s.getDeviceAddress())) {
                    if (s.getDeviceName() != null) {
                        namesFound.add(s.getDeviceName());
                        synchronized (discoveryCompletedEvent) {
                            discoveryCompletedEvent.notifyAll();
                        }
                    } else {
                        DebugLog.debug("device name is null");
                    }
                } else {
                    DebugLog.debug("ignore device name " + s.getDeviceAddress() + " " + s.getDeviceName());
                }
            }
        };

        try {
            dbusConn.addSigHandler(Adapter.DiscoveryCompleted.class, discoveryCompleted);
            dbusConn.addSigHandler(Adapter.RemoteNameUpdated.class, remoteNameUpdated);

            synchronized (discoveryCompletedEvent) {
                adapter.DiscoverDevices();
                DebugLog.debug("wait for device inquiry to complete...");
                try {
                    discoveryCompletedEvent.wait();
                    DebugLog.debug(namesFound.size() + " device name(s) found");
                    if (namesFound.size() == 0) {
                        throw new IOException("Can't retrive device name");
                    }
                    // return the last name found
                    return namesFound.get(namesFound.size() - 1);
                } catch (InterruptedException e) {
                    throw new InterruptedIOException();
                }
            }
        } finally {
            quietRemoveSigHandler(Adapter.RemoteNameUpdated.class, remoteNameUpdated);
            quietRemoveSigHandler(Adapter.DiscoveryCompleted.class, discoveryCompleted);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#retrieveDevices(boolean)
     */
    public List<String> retrieveDevices(boolean preKnown) {
        if (!preKnown) {
            return null;
        }
        List<String> addresses = new Vector<String>();
        String[] bonded = adapter.ListBondings();
        if (bonded != null) {
            for (int i = 0; i < bonded.length; i++) {
                addresses.add(bonded[i]);
            }
        }
        String[] trusted = adapter.ListTrusts();
        if (trusted != null) {
            for (int i = 0; i < trusted.length; i++) {
                addresses.add(trusted[i]);
            }
        }
        return addresses;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#isRemoteDeviceConnected(java.lang.String)
     */
    public boolean isRemoteDeviceConnected(String deviceAddress) throws DBusException {
        return adapter.IsConnected(deviceAddress);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#isRemoteDeviceTrusted(java.lang.String)
     */
    public Boolean isRemoteDeviceTrusted(String deviceAddress) throws DBusException {
        return Boolean.valueOf(adapter.HasBonding(deviceAddress));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#authenticateRemoteDevice(java.lang.String)
     */
    public void authenticateRemoteDevice(String deviceAddress) throws DBusException {
        adapter.CreateBonding(deviceAddress);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#authenticateRemoteDevice(java.lang.String,
     * java.lang.String)
     */
    public boolean authenticateRemoteDevice(final String deviceAddress, final String passkey) throws DBusException {
        if (passkey == null) {
            authenticateRemoteDevice(deviceAddress);
            return true;
        } else {

            PasskeyAgent passkeyAgent = new PasskeyAgent() {

                public String Request(String path, String address) throws Rejected, Canceled {
                    if (deviceAddress.equals(address)) {
                        DebugLog.debug("PasskeyAgent.Request");
                        return passkey;
                    } else {
                        return "";
                    }
                }

                public boolean isRemote() {
                    return false;
                }

                public void Cancel(String path, String address) {
                }

                public void Release() {
                }
            };

            //            final Object completedEvent = new Object();
            //            DBusSigHandler<Adapter.BondingCreated> bondingCreated = new DBusSigHandler<Adapter.BondingCreated>() {
            //                public void handle(Adapter.BondingCreated s) {
            //                    DebugLog.debug("BondingCreated.handle");
            //                    synchronized (completedEvent) {
            //                        completedEvent.notifyAll();
            //                    }
            //                }
            //            };

            DebugLog.debug("get security on path", adapterPath.getPath());
            Security security = dbusConn.getRemoteObject("org.bluez", adapterPath.getPath(), Security.class);

            String passkeyAgentPath = "/org/bluecove/authenticate/" + getAdapterID() + "/" + deviceAddress.replace(':', '_');

            DebugLog.debug("export passkeyAgent", passkeyAgentPath);
            dbusConn.exportObject(passkeyAgentPath, passkeyAgent);

            // see http://bugs.debian.org/cgi-bin/bugreport.cgi?bug=501222
            final boolean useDefaultPasskeyAgentBug = BlueCoveImpl.getConfigProperty("bluecove.bluez.registerDefaultPasskeyAgent", false);
            try {
                if (useDefaultPasskeyAgentBug) {
                    security.RegisterDefaultPasskeyAgent(passkeyAgentPath);
                } else {
                    security.RegisterPasskeyAgent(passkeyAgentPath, deviceAddress);
                }
                adapter.CreateBonding(deviceAddress);
                return true;
            } finally {
                //quietRemoveSigHandler(Adapter.BondingCreated.class, bondingCreated);
                try {
                    if (useDefaultPasskeyAgentBug) {
                        security.UnregisterDefaultPasskeyAgent(passkeyAgentPath);
                    } else {
                        security.UnregisterPasskeyAgent(passkeyAgentPath, deviceAddress);
                    }
                } catch (DBusExecutionException ignore) {
                }
                dbusConn.unExportObject(passkeyAgentPath);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#removeAuthenticationWithRemoteDevice(java.lang.String)
     */
    public void removeAuthenticationWithRemoteDevice(String deviceAddress) throws DBusException {
        adapter.RemoveBonding(deviceAddress);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#getRemoteDeviceServices(java.lang.String)
     */
    public Map<Integer, String> getRemoteDeviceServices(String deviceAddress) throws DBusException {
        String match = "";
        UInt32[] serviceHandles;
        try {
            serviceHandles = adapter.GetRemoteServiceHandles(deviceAddress, match);
        } catch (DBus.Error.NoReply e) {
            return null;
        }
        if (serviceHandles == null) {
            throw new DBusException("Recived no records");
        }
        Map<Integer, String> xmlRecords = new HashMap<Integer, String>();
        for (int i = 0; i < serviceHandles.length; ++i) {
            xmlRecords.put(serviceHandles[i].intValue(), adapter.GetRemoteServiceRecordAsXML(deviceAddress, serviceHandles[i]));
        }
        return xmlRecords;
    }

    private Database getSDPService() throws DBusException {
        //return dbusConn.getRemoteObject("org.bluez", adapterPath.getPath(), Database.class);
        return dbusConn.getRemoteObject("org.bluez", "/org/bluez", Database.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#registerSDPRecord(java.lang.String)
     */
    public long registerSDPRecord(String sdpXML) throws DBusException {
        DebugLog.debug("AddServiceRecordFromXML", sdpXML);
        UInt32 handle = getSDPService().AddServiceRecordFromXML(sdpXML);
        return handle.longValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#updateSDPRecord(long, java.lang.String)
     */
    public void updateSDPRecord(long handle, String sdpXML) throws DBusException {
        DebugLog.debug("UpdateServiceRecordFromXML", sdpXML);
        getSDPService().UpdateServiceRecordFromXML(new UInt32(handle), sdpXML);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bluez.BlueZAPI#unregisterSDPRecord(long)
     */
    public void unregisterSDPRecord(long handle) throws DBusException {
        DebugLog.debug("RemoveServiceRecord", handle);
        getSDPService().RemoveServiceRecord(new UInt32(handle));
    }
}
