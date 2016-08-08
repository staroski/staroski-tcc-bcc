/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2006-2008 Vlad Skarzhevskyy
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
 *  @version $Id: WIDCOMMStackRFCOMM.cpp 2484 2008-12-03 17:21:59Z skarzhevskyy $
 */

#include "WIDCOMMStack.h"

#ifdef _BTWLIB

#ifdef VC6
#define CPP_FILE "WIDCOMMStackRFCOMM.cpp"
#endif

static int openConnections = 0;

WIDCOMMStackRfCommPort::WIDCOMMStackRfCommPort() {

    server = NULL;

	readyForReuse();

    hDataReceivedEvent = CreateEvent(
            NULL,     // no security attributes
            FALSE,     // auto-reset event
            FALSE,     // initial state is NOT signaled
            NULL);    // object not named

    hDataTransmitEvent = CreateEvent(
            NULL,     // no security attributes
            FALSE,     // auto-reset event
            FALSE,     // initial state is NOT signaled
            NULL);    // object not named

	openConnections ++;
}

void WIDCOMMStackRfCommPort::readyForReuse() {
	resetReceiveBuffer();
	isConnected = FALSE;
	isConnectionError = FALSE;
	isConnectionErrorType = 0;
	other_event_code = 0;
	isClosing = FALSE;
	readyToFree = FALSE;
	service_name[0] = '\0';
}

void WIDCOMMStackRfCommPort::resetReceiveBuffer() {
	receiveBuffer.reset();
}

WIDCOMMStackRfCommPort::~WIDCOMMStackRfCommPort() {
	if (isConnected) {
		isClosing = TRUE;
		SetEvent(hConnectionEvent);
		Close();
	}
	isConnected = FALSE;

    CloseHandle(hDataTransmitEvent);
	CloseHandle(hDataReceivedEvent);
	openConnections --;
}

void WIDCOMMStackRfCommPort::OnEventReceived (UINT32 event_code) {
    if (stack == NULL) {
		return;
	}
	if ((magic1 != MAGIC_1) || (magic2 != MAGIC_2) || isClosing || (!isValidStackObject(this))) {
	    ndebug(("e.rf(%i) OnEventReceived for invlaid object, event_code 0x%x", internalHandle, event_code));
		return;
	}
	if (PORT_EV_CONNECTED & event_code) {
	    if (isConnectionError) {
	        ndebug(("rf(%i) OnEventReceived connected event 0x%x on broken connection", internalHandle, event_code));
	        return;
	    }
	    ndebug(("rf(%i) OnEventReceived connected event 0x%x", internalHandle, event_code));
	    BD_ADDR connected_bd_addr;
        if (IsConnected(&connected_bd_addr)) {
			isConnected = TRUE;
			SetEvent(hConnectionEvent);
		} else {
		    ndebug(("rf(%i) OnEventReceived not yet connected", internalHandle));
		}
	} else if (PORT_EV_CONNECT_ERR & event_code) {
	    ndebug(("e.rf(%i) OnEventReceived Connect error 0x%x", internalHandle, event_code));
		isConnectionErrorType = 1;
		isConnectionError = TRUE;
		isConnected = FALSE;
		SetEvent(hConnectionEvent);
	} else if (PORT_EV_OVERRUN & event_code) {
		ndebug(("e.rf(%i) OnEventReceived Overrun, 0x%x", internalHandle, event_code));
		isConnectionErrorType = 2;
		isConnectionError = TRUE;
		receiveBuffer.setOverflown();
		SetEvent(hConnectionEvent);
	} else if (PORT_EV_TXCHAR & event_code) {
	    // Any character transmitted
	    SetEvent(hDataTransmitEvent);
	} else if (PORT_EV_TXEMPTY & event_code) {
	    // Transmit Queue Empty
	    // TODO use in flush();
	    SetEvent(hDataTransmitEvent);
	} else if ((!isConnected) && (!isConnectionError) && (PORT_EV_RXFLAG & event_code)) {
	    ndebug(("e.rf(%i) OnEventReceived not yet connected, 0x%x", internalHandle, event_code));
	    isConnectionErrorType = 3;
		isConnectionError = TRUE;
		SetEvent(hConnectionEvent);
	} else {
	    ndebug(("rf(%i) OnEventReceived event_code 0x%x", internalHandle, event_code));
		other_event_code = event_code;
		SetEvent(hConnectionEvent);
	}
}

void WIDCOMMStackRfCommPort::OnDataReceived(void *p_data, UINT16 len) {
    if (stack == NULL) {
		return;
	}
	if ((magic1 != MAGIC_1) || (magic2 != MAGIC_2) || isClosing || (!isValidStackObject(this))) {
	    ndebug(("e.rf(%i) OnDataReceived for invlaid object", internalHandle));
		return;
	}
	if (isConnected && !isClosing) {
		receiveBuffer.write(p_data, len);
		SetEvent(hDataReceivedEvent);
	}
}

#define open_client_return  open_client_finally(rf); return

void open_client_finally(WIDCOMMStackRfCommPort* rf) {
	if ((rf != NULL) && (stack != NULL)) {
		// Just in case Close
		rf->Close();
		stack->deleteConnection(rf);
	}
	if (stack != NULL) {
		LeaveCriticalSection(&stack->csCommIf);
	}
}

JNIEXPORT jlong JNICALL Java_com_intel_bluetooth_BluetoothStackWIDCOMM_connectionRfOpenClientConnectionImpl
(JNIEnv *env, jobject peer, jlong address, jint channel, jboolean authenticate, jboolean encrypt, jint timeout) {
	BD_ADDR bda;
	LongToBcAddr(address, bda);

	WIDCOMMStackRfCommPort* rf = NULL;
	EnterCriticalSection(&stack->csCommIf);
	//vc6 __try {
	    if (stack != NULL) {
		    rf = stack->createCommPort();
	    }
		if (rf == NULL) {
			throwBluetoothConnectionException(env, BT_CONNECTION_ERROR_NO_RESOURCES, "No free connections Objects in Pool");
			open_client_return 0;
		}
        debug(("rf(%i) RfCommPort client handle created", rf->internalHandle));
		if ((rf->hConnectionEvent == NULL) || (rf->hDataReceivedEvent == NULL)) {
			throwRuntimeException(env, "fails to CreateEvent");
			open_client_return 0;
		}
		debug(("rf(%i) RfCommPort channel %i", rf->internalHandle, channel));
		CRfCommIf* rfCommIf = &(stack->rfCommIfClient);

		//debug(("AssignScnValue");
		// What GUID do we need in call to CRfCommIf.AssignScnValue() if we don't have any?
		//memcpy(&(rf->service_guid), &test_client_service_guid, sizeof(GUID));
		if (!rfCommIf->AssignScnValue(&(rf->service_guid), (UINT8)channel)) {
			throwBluetoothConnectionException(env, BT_CONNECTION_ERROR_UNKNOWN_PSM, "failed to assign SCN %i", (UINT8)channel);
			open_client_return 0;
		}
		//debug(("SetSecurityLevel");
		UINT8 sec_level = BTM_SEC_NONE;
		if (authenticate) {
			sec_level = BTM_SEC_OUT_AUTHENTICATE;
		}
		if (encrypt) {
			sec_level = sec_level | BTM_SEC_OUT_ENCRYPT;
		}

		BT_CHAR *p_service_name;

		#ifdef _WIN32_WCE
			p_service_name = (BT_CHAR*)L"bluecovesrv";
		#else // _WIN32_WCE
			p_service_name = "bluecovesrv";
		#endif // #else // _WIN32_WCE

		if (!rfCommIf->SetSecurityLevel(p_service_name, sec_level, FALSE)) {
			throwBluetoothConnectionException(env, BT_CONNECTION_ERROR_SECURITY_BLOCK, "Error setting security level");
			open_client_return 0;
        }
		Edebug(("OpenClient"));
		CRfCommPort::PORT_RETURN_CODE rc = rf->OpenClient((UINT8)channel, bda);
		if (rc != CRfCommPort::SUCCESS) {
			if (rc == CRfCommPort::PEER_TIMEOUT) {
				throwBluetoothConnectionException(env, BT_CONNECTION_ERROR_TIMEOUT, "Failed to OpenClient");
			} else {
				throwBluetoothConnectionException(env, BT_CONNECTION_ERROR_FAILED_NOINFO, "Failed to OpenClient [%i]", rc);
			}
			open_client_return 0;
		}

		Edebug(("waitFor Connection signal"));
		DWORD waitStart = GetTickCount();
		while ((stack != NULL) && !rf->isClosing && !rf->isConnected && !rf->isConnectionError) {
			DWORD  rc = WaitForSingleObject(rf->hConnectionEvent, 500);
			if (rc == WAIT_FAILED) {
				throwRuntimeException(env, "WaitForSingleObject");
				open_client_return 0;
			}
			if (isCurrentThreadInterrupted(env, peer)) {
			    debug(("Interrupted while writing"));
			    open_client_return 0;
		    }
			if ((timeout > 0) && ((GetTickCount() - waitStart)  > (DWORD)timeout)) {
				throwBluetoothConnectionException(env, BT_CONNECTION_ERROR_TIMEOUT, "Connection timeout");
				open_client_return 0;
			}
		}
		if ((stack == NULL) || rf->isClosing || rf->isConnectionError) {
		    if ((stack != NULL) && (rf->isConnectionError)) {
		        debug(("RfCommPort isConnectionError %i", rf->isConnectionErrorType));
		    }
			throwBluetoothConnectionException(env, BT_CONNECTION_ERROR_FAILED_NOINFO, "Failed to connect");
			open_client_return 0;
		}
		debug(("rf(%i) rfClientConnected", rf->internalHandle));
		//rf->SetFlowEnabled(TRUE);

		jlong handle = rf->internalHandle;
		rf = NULL;
		open_client_return handle;
	/* vc6 } __finally {
		if ((rf != NULL) && (stack != NULL)) {
			// Just in case Close
			rf->Close();
			stack->deleteCommPort(rf);
		}
		LeaveCriticalSection(&stack->csCommIf);
	}*/
}

void WIDCOMMStackRfCommPort::close(JNIEnv *env, BOOL allowExceptions) {
	isClosing = TRUE;
	server = NULL;
	SetEvent(hConnectionEvent);
	debug(("rf(%i) closing RfCommPort, Connected[%s]", internalHandle, bool2str(isConnected)));
	Purge(PORT_PURGE_TXCLEAR);
	Purge(PORT_PURGE_RXCLEAR);
	CRfCommPort::PORT_RETURN_CODE rc = Close();
	if (rc != CRfCommPort::SUCCESS && rc != CRfCommPort::NOT_OPENED) {
		if (allowExceptions) {
		    throwIOException(env, "Failed to Close");
	    }
	} else {
		debug(("rf(%i) closed RfCommPort", internalHandle));
	}
}

JNIEXPORT void JNICALL Java_com_intel_bluetooth_BluetoothStackWIDCOMM_closeRfCommPortImpl
(JNIEnv *env, jobject peer, jlong handle) {
	WIDCOMMStackRfCommPort* rf = validRfCommHandle(env, handle);
	if (rf == NULL) {
		return;
	}
	//debug(("CloseClientConnection"));
	rf->close(env, true);
	// Some worker thread is still trying to access this object, delete later
	if (stack != NULL) {
		stack->deleteConnection(rf);
	}
	//debug(("connection handles %i", openConnections));
}

JNIEXPORT jlong JNICALL Java_com_intel_bluetooth_BluetoothStackWIDCOMM_getConnectionRfRemoteAddress
(JNIEnv *env, jobject peer, jlong handle) {
	WIDCOMMStackRfCommPort* rf = validRfCommHandle(env, handle);
	if (rf == NULL) {
		return 0;
	}
	if (!rf->isConnected || rf->isClosing) {
		throwIOException(env, cCONNECTION_IS_CLOSED);
		return 0;
	}
	BD_ADDR connected_bd_addr;
    if (rf->IsConnected(&connected_bd_addr)) {
		return BcAddrToLong(connected_bd_addr);
	} else {
		throwIOException(env, "Connection down");
		return 0;
	}
}

JNIEXPORT jint JNICALL Java_com_intel_bluetooth_BluetoothStackWIDCOMM_connectionRfRead__J
(JNIEnv *env, jobject peer, jlong handle) {
	WIDCOMMStackRfCommPort* rf = validRfCommHandle(env, handle);
	if (rf == NULL) {
		return -1;
	}
	debug(("rf(%i) read()", rf->internalHandle));
	if (rf->isClosing) {
		return -1;
	}
	if (rf->receiveBuffer.isOverflown()) {
		throwIOException(env, "Receive buffer overflown");
		return 0;
	}
	HANDLE hEvents[2];
	hEvents[0] = rf->hConnectionEvent;
	hEvents[1] = rf->hDataReceivedEvent;
	BOOL debugOnce = TRUE;
	while ((stack != NULL) && rf->isConnected && (!rf->isClosing) && (rf->receiveBuffer.available() == 0)) {
		if (debugOnce) {
			debug(("read() waits for data"));
			debugOnce = FALSE;
		}
		DWORD  rc = WaitForMultipleObjects(2, hEvents, FALSE, 500);
		if (rc == WAIT_FAILED) {
			throwRuntimeException(env, "WaitForMultipleObjects");
			return 0;
		}
		if (isCurrentThreadInterrupted(env, peer)) {
			return 0;
		}
		/*
		if (rf->other_event_code != 0) {
			debug(("connectionEvent %i", rf->other_event_code);
			rf->other_event_code = 0;
		}
		debug2("isClosing [%s], isConnected[%s]", bool2str(rf->isClosing), bool2str(rf->isConnected));
		*/
	}
	if ((stack == NULL) || (rf->isClosing) || (rf->receiveBuffer.available() == 0)) {
		// See InputStream.read();
		return -1;
	}
	return rf->receiveBuffer.readByte();
}

JNIEXPORT jint JNICALL Java_com_intel_bluetooth_BluetoothStackWIDCOMM_connectionRfRead__J_3BII
(JNIEnv *env, jobject peer, jlong handle, jbyteArray b, jint off, jint len) {
	WIDCOMMStackRfCommPort* rf = validRfCommHandle(env, handle);
	if (rf == NULL) {
		return -1;
	}
	debug(("rf(%i) read(byte[%i])", rf->internalHandle, len));
	if (rf->isClosing) {
		debug(("read([]) isClosing"));
		return -1;
	}
	if (rf->receiveBuffer.isOverflown()) {
		throwIOException(env, "Receive buffer overflown");
		return 0;
	}

	jbyte *bytes = env->GetByteArrayElements(b, 0);

	HANDLE hEvents[2];
	hEvents[0] = rf->hConnectionEvent;
	hEvents[1] = rf->hDataReceivedEvent;

	int done = 0;

	BOOL debugOnce = TRUE;
	while ((stack != NULL) && rf->isConnected && (!rf->isClosing) && (done < len)) {
		while ((stack != NULL) && rf->isConnected  && (!rf->isClosing) && (rf->receiveBuffer.available() == 0)) {
			if (debugOnce) {
				debug(("read[] waits for data"));
				debugOnce = FALSE;
			}
			DWORD  rc = WaitForMultipleObjects(2, hEvents, FALSE, 500);
			if (rc == WAIT_FAILED) {
				env->ReleaseByteArrayElements(b, bytes, 0);
				throwRuntimeException(env, "WaitForMultipleObjects");
				return 0;
			}
			if (rc != WAIT_TIMEOUT) {
				debug(("read waits returns %s", waitResultsString(rc)));
			}
			if (isCurrentThreadInterrupted(env, peer)) {
				debug(("Interrupted while reading"));
				return 0;
			}
		}
		if (stack == NULL) {
			env->ReleaseByteArrayElements(b, bytes, 0);
			return -1;
		}
		int count = rf->receiveBuffer.available();
		if (count > 0) {
			if (count > len - done) {
				count = len - done;
			}
			done += rf->receiveBuffer.read(bytes + off + done, count);
		}
		if (done != 0) {
		    // Don't do readFully!
		    break;
		}
		debug(("rf(%i) read([]) received %i", rf->internalHandle, count));
	}

	if (!rf->isConnected) {
		debug(("rf(%i) read([]) not connected", rf->internalHandle));
	}
	// Read from not Connected
	int count = rf->receiveBuffer.available();
	if (count > 0) {
		if (count > len - done) {
			count = len - done;
		}
		done += rf->receiveBuffer.read(bytes + off + done, count);
		debug(("read[] available %i", done));
	}

	if ((stack == NULL) || (rf->isClosing) || (!rf->isConnected && done == 0)) {
		if (done == 0) {
			debug(("read([]) no data"));
		}
		// See InputStream.read();
		debug(("read([]) return EOF"));
		done = -1;
	} else {
		debug(("read([]) return %i", done));
	}
	env->ReleaseByteArrayElements(b, bytes, 0);
	return done;
}

JNIEXPORT jint JNICALL Java_com_intel_bluetooth_BluetoothStackWIDCOMM_connectionRfReadAvailable
(JNIEnv *env, jobject peer, jlong handle) {
	WIDCOMMStackRfCommPort* rf = validRfCommHandle(env, handle);
	if (rf == NULL || rf->isClosing) {
		return 0;
	}
	if (rf->receiveBuffer.isOverflown()) {
		throwIOException(env, "Receive buffer overflown");
	}
	return rf->receiveBuffer.available();
}

JNIEXPORT void JNICALL Java_com_intel_bluetooth_BluetoothStackWIDCOMM_connectionRfWriteImpl
(JNIEnv *env, jobject peer, jlong handle, jbyteArray b, jint off, jint len) {
	WIDCOMMStackRfCommPort* rf = validRfCommHandle(env, handle);
	if (rf == NULL) {
		return;
	}
	debug(("rf(%i) write(byte[%i])", rf->internalHandle, len));
	if (len > 0x10000) {
	    throwRuntimeException(env, "64K Array max");
	    return;
	}
	if (!rf->isConnected || rf->isClosing) {
		throwIOException(env, cCONNECTION_IS_CLOSED);
		return;
	}

	jbyte *bytes = env->GetByteArrayElements(b, 0);

	jint done = 0;

	while ((rf->isConnected) && (!rf->isClosing)) {
		UINT8 signal;
		rf->GetModemStatus(&signal);
		if (signal & PORT_CTSRTS_ON) {
			break;
		}
		Sleep(200);
		if (isCurrentThreadInterrupted(env, peer)) {
		    return;
		}
	}
    HANDLE hEvents[2];
	hEvents[0] = rf->hConnectionEvent;
	hEvents[1] = rf->hDataTransmitEvent;

	BOOL waitForTransmit = false;

	while ((done < len) && rf->isConnected && (!rf->isClosing)) {
	    if (waitForTransmit) {
	        // second write since not all data was written by first call
	        debug(("rf(%i) write(byte[%i]) continue from [%i]", rf->internalHandle, len, done));
	        DWORD  rc = WaitForMultipleObjects(2, hEvents, FALSE, 500);
			if (rc == WAIT_FAILED) {
				env->ReleaseByteArrayElements(b, bytes, 0);
				throwRuntimeException(env, "WaitForMultipleObjects");
				return;
			}
			if (rc != WAIT_TIMEOUT) {
				debug(("write waits returns %s", waitResultsString(rc)));
			}
	    }
		UINT16 written = 0;
		UINT16 write_len = (UINT16)(len - done);
		CRfCommPort::PORT_RETURN_CODE rc = rf->Write((void*)(bytes + off + done), write_len, &written);
		if ((rc != CRfCommPort::SUCCESS) || (written > write_len)) {
			throwIOException(env, "Failed to write");
			break;
		}
		done += written;
		if (isCurrentThreadInterrupted(env, peer)) {
			break;
		}
		waitForTransmit = true;
	}

	env->ReleaseByteArrayElements(b, bytes, 0);
}


WIDCOMMStackRfCommPortServer::WIDCOMMStackRfCommPortServer() {
}

WIDCOMMStackRfCommPortServer::~WIDCOMMStackRfCommPortServer() {
}

#define open_rf_server_return  open_rf_server_finally(env, rf); return

void open_rf_server_finally(JNIEnv *env, WIDCOMMStackRfCommPortServer* rf) {
	if ((rf != NULL) && (stack != NULL)) {
		rf->close(env, false);
		stack->deleteConnection(rf);
	}

	if (stack != NULL) {
		LeaveCriticalSection(&stack->csCommIf);
	}
}

JNIEXPORT jlong JNICALL Java_com_intel_bluetooth_BluetoothStackWIDCOMM_rfServerOpenImpl
(JNIEnv *env, jobject peer, jbyteArray uuidValue, jbyteArray uuidValue2, jboolean obexSrv, jstring name, jboolean authenticate, jboolean encrypt) {
    if (stack == NULL) {
		throwIOException(env, cSTACK_CLOSED);
		return 0;
	}
	EnterCriticalSection(&stack->csCommIf);
	WIDCOMMStackRfCommPortServer* rf = NULL;
//VC6	__try {

		rf = (WIDCOMMStackRfCommPortServer*)stack->createCommServer();
		if (rf == NULL) {
			throwIOException(env, "No free connections Objects in Pool");
			open_rf_server_return 0;
		}
		const char *cname = env->GetStringUTFChars(name, 0);
		#ifdef _WIN32_WCE
			swprintf_s((wchar_t*)rf->service_name, BT_MAX_SERVICE_NAME_LEN, L"%s", cname);
		#else // _WIN32_WCE
			sprintf_s(rf->service_name, BT_MAX_SERVICE_NAME_LEN, "%s", cname);
		#endif // #else // _WIN32_WCE
		env->ReleaseStringUTFChars(name, cname);

		jbyte *bytes = env->GetByteArrayElements(uuidValue, 0);
		// build UUID
		convertUUIDBytesToGUID(bytes, &(rf->service_guid));
		env->ReleaseByteArrayElements(uuidValue, bytes, 0);

		CRfCommIf* rfCommIf = &(rf->rfCommIf);

		BOOL assignScnRC;
		#ifdef _WIN32_WCE
			assignScnRC = rfCommIf->AssignScnValue(&(rf->service_guid), (UINT8)0);
		#else // _WIN32_WCE
			assignScnRC = rfCommIf->AssignScnValue(&(rf->service_guid), 0, rf->service_name);
		#endif // #else // _WIN32_WCE

		if (!assignScnRC) {
			throwIOException(env, "failed to assign SCN");
			open_rf_server_return 0;
		}
		rf->scn = rfCommIf->GetScn();

        rf->service_guids_len = 1;
        if (uuidValue2 != NULL) {
            rf->service_guids_len = 2;
        }
        rf->service_guids = new GUID[rf->service_guids_len];
        if (rf->service_guids == NULL) {
            throwIOException(env, cOUT_OF_MEMORY);
            open_rf_server_return 0;
        }
		memcpy(&(rf->service_guids[0]), &(rf->service_guid), sizeof(GUID));
		if (uuidValue2 != NULL) {
			jbyte *bytes2 = env->GetByteArrayElements(uuidValue2, 0);
			convertUUIDBytesToGUID(bytes2, &(rf->service_guids[1]));
			env->ReleaseByteArrayElements(uuidValue2, bytes2, 0);
		}
		/*
		  Can't use AddRFCommProtocolDescriptor
		  An L2Cap UUID (100) with a value of 3 is added because RFCOMM protocol is over the L2CAP protocol.
		  build the proto_elem_list
		*/
		rf->proto_num_elem = 2;
		if (obexSrv) {
			rf->proto_num_elem++;
		}

		rf->proto_elem_list = new tSDP_PROTOCOL_ELEM[rf->proto_num_elem];
		if (rf->proto_elem_list == NULL) {
            throwIOException(env, cOUT_OF_MEMORY);
            open_rf_server_return 0;
        }
		rf->proto_elem_list[0].protocol_uuid = 0x0100; // L2CAP
		rf->proto_elem_list[0].num_params = 0;

		rf->proto_elem_list[1].protocol_uuid = 0x0003; // RFCOMM
		rf->proto_elem_list[1].num_params = 1;
		rf->proto_elem_list[1].params[0] = rf->scn;

		if (obexSrv) {
			rf->proto_elem_list[2].protocol_uuid = 0x0008; // OBEX
			rf->proto_elem_list[2].num_params = 0;
		}

		UINT8 sec_level = BTM_SEC_NONE;
		if (authenticate) {
			sec_level = BTM_SEC_IN_AUTHENTICATE;
		}

		if (encrypt) {
			sec_level = sec_level | BTM_SEC_IN_ENCRYPT;
		}

		if (!rfCommIf->SetSecurityLevel(rf->service_name, sec_level, TRUE)) {
			throwIOException(env, "Error setting security level");
			open_rf_server_return 0;
        }

		jlong handle = rf->internalHandle;
		debug(("rfs(%i) ServerOpen", rf->internalHandle));
		rf = NULL;
		open_rf_server_return handle;
/*VC6
	} __finally {
		if ((rf != NULL) && (stack != NULL)) {
			rf->closeRfCommPort(env);
			stack->deleteCommPort(rf);
		}
		LeaveCriticalSection(&stack->csCommIf);
	}
*/
}

JNIEXPORT void JNICALL Java_com_intel_bluetooth_BluetoothStackWIDCOMM_rfServerCloseImpl
(JNIEnv *env, jobject peer, jlong handle) {
	WIDCOMMStackRfCommPortServer* srv = validRfCommServerHandle(env, handle);
	if (srv == NULL) {
		return;
	}
	debug(("rfs(%i) rfCloseServer", srv->internalHandle));
	srv->close(env, true);
	// Some worker thread is still trying to access this object, delete later
	if (stack != NULL) {
		stack->deleteConnection(srv);
	}
	//debug(("connection handles %i", openConnections));
}

#define accept_rf_server_return  accept_rf_server_finally(env, rf); return

void accept_rf_server_finally(JNIEnv *env, WIDCOMMStackRfCommPort* rf) {
	if ((stack != NULL) && (rf != NULL)) {
	    if (rf->server != NULL) {
	        rf->server->closeClient(env, rf);
	    }
	    if (stack != NULL) {
		    stack->deleteConnection(rf);
	    }
	}
}

JNIEXPORT jlong JNICALL Java_com_intel_bluetooth_BluetoothStackWIDCOMM_rfServerAcceptAndOpenRfServerConnection
(JNIEnv *env, jobject peer, jlong handle) {
	debug(("rfs(%i) acceptAndOpen", handle));
	WIDCOMMStackRfCommPortServer* srv = validRfCommServerHandle(env, handle);
	if (srv == NULL) {
		return 0;
	}
	if (srv->sdpService == NULL) {
		throwIOException(env, cCONNECTION_IS_CLOSED);
		return 0;
	}
    if (!srv->finalizeSDPRecord(env)) {
        return 0;
    }

	#ifdef BWT_SINCE_SDK_6_0_1
	srv->sdpService->CommitRecord();
	#endif

	EnterCriticalSection(&stack->csCommIf);
	if (stack == NULL) {
		throwInterruptedIOException(env, cSTACK_CLOSED);
	}

	WIDCOMMStackRfCommPort* rf = stack->createCommPort();
	if (rf == NULL) {
		throwIOException(env, "No free connections Objects in Pool");
		return 0;
	}
    srv->addClient(rf);

	CRfCommPort::PORT_RETURN_CODE rc = rf->OpenServer(srv->scn);
	if (stack != NULL) {
		LeaveCriticalSection(&stack->csCommIf);
	}

	if (rc != CRfCommPort::SUCCESS) {
		throwIOException(env, "Failed to OpenServer");
		accept_rf_server_return 0;
	}

	HANDLE hEvents[2];
	hEvents[0] = rf->hConnectionEvent;
	hEvents[1] = srv->hConnectionEvent;

	debug(("rfs(%i) rf(%i) RFCOMM server waits for connection", srv->internalHandle, rf->internalHandle));
	while ((stack != NULL) && (!srv->isClosing)  && (!rf->isConnected) && (!rf->isConnectionError) && (srv->sdpService != NULL)) {
		DWORD  rc = WaitForMultipleObjects(2, hEvents, FALSE, 500);
		if (rc == WAIT_FAILED) {
			throwRuntimeException(env, "WaitForMultipleObjects");
			accept_rf_server_return 0;
		}
		if (isCurrentThreadInterrupted(env, peer)) {
			accept_rf_server_return 0;
		}
	}

	if ((stack == NULL) || srv->isClosing || rf->isConnectionError || (srv->sdpService == NULL)) {
		if (stack == NULL) {
			throwInterruptedIOException(env, cSTACK_CLOSED);
		} else if (srv->isClosing || (srv->sdpService == NULL)) {
			throwInterruptedIOException(env, cCONNECTION_CLOSED);
		} else if (rf->isConnectionError) {
			throwIOException(env, "Connection error");
		} else {
			throwIOException(env, "Failed to connect");
		}
		accept_rf_server_return 0;
	}
	debug(("rfs(%i) rf(%i) RFCOMM server connection made", srv->internalHandle, rf->internalHandle));
	return rf->internalHandle;
}

JNIEXPORT jint JNICALL Java_com_intel_bluetooth_BluetoothStackWIDCOMM_rfServerSCN
(JNIEnv *env, jobject, jlong handle) {
	WIDCOMMStackRfCommPortServer* srv = validRfCommServerHandle(env, handle);
	if (srv == NULL) {
		return 0;
	}
	return srv->scn;
}

JNIEXPORT void JNICALL Java_com_intel_bluetooth_BluetoothStackWIDCOMM_connectionRfCloseServerConnection
(JNIEnv *env, jobject, jlong handle) {
	WIDCOMMStackRfCommPort* rf = validRfCommHandle(env, handle);
	if (rf == NULL) {
		return;
	}
	debug(("rf(%i) CloseServerConnection", rf->internalHandle));
	WIDCOMMStackServerConnectionBase* srv = rf->server;
	if (srv != NULL) {
	    srv->closeClient(env, rf);
	} else {
	    rf->close(env, true);
    }
	if (stack != NULL) {
		stack->deleteConnection(rf);
	}
}

#endif //  _BTWLIB