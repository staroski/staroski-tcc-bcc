/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2007 Vlad Skarzhevskyy
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
 *  @version $Id: Service.java 2471 2008-12-01 03:44:20Z skarzhevskyy $
 */
package org.bluez;

import org.freedesktop.dbus.DBusInterface;

/**
 * The service interfaces aim provide an easy way to develop Bluetooth services.
 * For remote service search(handles/records) check the Adapter interface. This
 * section is restricted to local interactions only. There are two interfaces
 * related to Bluetooth services:
 * <p>
 * <ul>
 * <li>Service interface: retrieve information about the registered service.
 * eg: name, description, status, Bus id, ...</li>
 * <li>Database interface: manage SDP records and service connection
 * authorizations </li>
 * <ul>
 * 
 * BlueZ services can be classified as external and internal. Internal are
 * services registered automatically when the system starts. External are
 * services running in the standalone mode where the user start the service and
 * ask for registration. Once the service is registered, an object instance will
 * be available, the methods provided are described below.
 * 
 * Service org.bluez; Interface org.bluez.Service; Object path path from
 * org.bluez.Manager.ListServices()
 * 
 * 
 */
public interface Service extends DBusInterface {
/*
 
 dict GetInfo()

			Returns the service properties.

		String GetIdentifier()

			This method returns the service identifier.

		String GetName()

			This method returns the service name.

		String GetDescription()

			This method returns the service description.

		String GetBusName() [experimental]

			Returns the unique bus name of the service if it has
			been started.

			throws Error.NotAvailable

		void Start()

			This method tells the system to start the
			service.

		void Stop()

			This method tells the system to stop the
			service.

		boolean IsRunning()

			Returns true if the service has been started and
			is currently active. Otherwise, it returns false.

		boolean IsExternal()

			Returns true if the service was registered using the
			Database.RegisterService method instead of a .service
			file. The Start and Stop methods are not applicable to
			external services and will return an error.

		array{String} ListTrusts() [experimental]

			Returns a list of remote devices that are trusted
			for the service.

		void SetTrusted(String address) [experimental]

			Marks the user as trusted.

			throws Error.InvalidArguments
					 Error.AlreadyExists

		boolean IsTrusted(String address) [experimental]

			Returns true if the user is trusted or false otherwise.
			The address parameter must match one of the
			current users of the service.

			throws Error.InvalidArguments

		void RemoveTrust(String address) [experimental]

			Marks the user as not trusted.

			throws Error.InvalidArguments
					 Error.DoesNotExist

Signals		void Started()

			The object path of this signal contains which service
			was started.

		void Stopped()

			The object path of this signal contains which service
			was stopped.

		void TrustAdded(String address)

			Sent when SetTrusted() is called.

		void TrustRemoved(String address)

			Sent when RemoveTrust() is called.
 
 */
}
