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
 *  @version $Id: BlueZAPIFactory.java 2892 2009-03-11 20:20:07Z skarzhevskyy $
 */
package org.bluez;

import org.bluez.v3.BlueZAPIV3;
import org.bluez.v4.BlueZAPIV4;
import org.freedesktop.DBus;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

import com.intel.bluetooth.DebugLog;

/**
 * Abstraction interface factory to access BlueZ over D-Bus
 * 
 * Detect BlueZ D-Bus interface version and creates appropriate abstraction layer for
 * JSR-82 implementation.
 */
public abstract class BlueZAPIFactory {

    private BlueZAPIFactory() {

    }

    /**
     * This does BlueZ version detection
     * 
     * @param dbusConn
     * @return abstraction interface
     * @throws DBusException
     */
    public static BlueZAPI getBlueZAPI(DBusConnection dbusConn) throws DBusException {
        org.bluez.v3.Manager dbusManagerV3;
        dbusManagerV3 = dbusConn.getRemoteObject("org.bluez", "/org/bluez", org.bluez.v3.Manager.class);
        try {
            dbusManagerV3.InterfaceVersion();
            return new BlueZAPIV3(dbusConn, dbusManagerV3);
        } catch (DBus.Error.UnknownMethod ok) {
            DebugLog.debug("Switch to bluez D-Bus for version 4");
            org.bluez.v4.Manager dbusManagerV4 = dbusConn.getRemoteObject("org.bluez", "/", org.bluez.v4.Manager.class);
            return new BlueZAPIV4(dbusConn, dbusManagerV4);
        }
    }
}
