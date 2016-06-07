/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2007-2008 Vlad Skarzhevskyy
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
 *  @version $Id: Error.java 2471 2008-12-01 03:44:20Z skarzhevskyy $
 */
package org.bluez;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.exceptions.DBusExecutionException;

/**
 * 
 * Created base on D-Bus API description for BlueZ. bluez-utils-3.17/hcid/dbus-api.txt
 * 
 */
public interface Error extends DBusInterface {

	/**
	 * An unknown error occured. The error messages is taken from the
	 * strerror(errno) function.
	 * 
	 */
	@SuppressWarnings("serial")
	public class Failed extends DBusExecutionException {
		public Failed(String message) {
			super(message);
		}
	}
	
	/**
	 * Error returned when the argument list is invalid or out of specification
	 * for the method.
	 * 
	 */
	@SuppressWarnings("serial")
	public class InvalidArguments extends DBusExecutionException {
		public InvalidArguments(String message) {
			super(message);
		}
	}

	/**
	 * Error returned when the caller of a method is not authorized. This might
	 * happen if a caller tries to terminate a connection that it hasn't
	 * created.
	 */
	@SuppressWarnings("serial")
	public class NotAuthorized extends DBusExecutionException {
		public NotAuthorized(String message) {
			super(message);
		}
	}

	/**
	 * Error returned when a memory allocation via malloc() fails. This error is
	 * similar to ENOMEM.
	 */
	@SuppressWarnings("serial")
	public class OutOfMemory extends DBusExecutionException {
		public OutOfMemory(String message) {
			super(message);
		}
	}

	/**
	 * Error returned when the requested adapter doesn't exists. This error is
	 * similar to ENODEV.
	 */
	@SuppressWarnings("serial")
	public class NoSuchAdapter extends DBusExecutionException {
		public NoSuchAdapter(String message) {
			super(message);
		}
	}

	/**
	 * Error returned when the adapter is DOWN.
	 */
	@SuppressWarnings("serial")
	public class NotReady extends DBusExecutionException {
		public NotReady(String message) {
			super(message);
		}
	}

	/**
	 * Error returned when a specified record is not available.
	 */
	@SuppressWarnings("serial")
	public class NotAvailable extends DBusExecutionException {
		public NotAvailable(String message) {
			super(message);
		}
	}

	/**
	 * Error returned when the remote device isn't connected at the moment.
	 */
	@SuppressWarnings("serial")
	public class NotConnected extends DBusExecutionException {
		public NotConnected(String message) {
			super(message);
		}
	}

	@SuppressWarnings("serial")
	public class ConnectionAttemptFailed extends DBusExecutionException {
		public ConnectionAttemptFailed(String message) {
			super(message);
		}
	}

	/**
	 * Error returned if a record for a specific procedure already exists and it
	 * has been tried create a new one. The error message however should
	 * indicate the procedure that fails. For example "Bonding already exists"
	 */
	@SuppressWarnings("serial")
	public class AlreadyExists extends DBusExecutionException {
		public AlreadyExists(String message) {
			super(message);
		}
	}

	/**
	 * Error returned if a record for a specifc procedure doesn't exist. The
	 * error message however should indicate the procedure that fails. For
	 * example "Bonding does not exist".
	 */
	@SuppressWarnings("serial")
	public class DoesNotExist extends DBusExecutionException {
		public DoesNotExist(String message) {
			super(message);
		}
	}

	/**
	 * Error returned if an operation is in progress. Since this is a generic
	 * error that can be used in various situations, the error message should be
	 * more clear about what is in progress. For example "Bonding in progress".
	 */
	@SuppressWarnings("serial")
	public class InProgress extends DBusExecutionException {
		public InProgress(String message) {
			super(message);
		}
	}

	/**
	 * The feature is not supported by the remote device
	 */
	@SuppressWarnings("serial")
	public class NotSupported extends DBusExecutionException {
		public NotSupported(String message) {
			super(message);
		}
	}

	@SuppressWarnings("serial")
	public class AuthenticationFailed extends DBusExecutionException {
		public AuthenticationFailed(String message) {
			super(message);
		}
	}

	@SuppressWarnings("serial")
	public class AuthenticationTimeout extends DBusExecutionException {
		public AuthenticationTimeout(String message) {
			super(message);
		}
	}

	@SuppressWarnings("serial")
	public class AuthenticationRejected extends DBusExecutionException {
		public AuthenticationRejected(String message) {
			super(message);
		}
	}

	@SuppressWarnings("serial")
	public class AuthenticationCanceled extends DBusExecutionException {
		public AuthenticationCanceled(String message) {
			super(message);
		}
	}

	@SuppressWarnings("serial")
	public class UnsupportedMajorClass extends DBusExecutionException {
		public UnsupportedMajorClass(String message) {
			super(message);
		}
	}
	
	@SuppressWarnings("serial")
	public class NoSuchService extends DBusExecutionException {
		public NoSuchService(String message) {
			super(message);
		}
	}

}
