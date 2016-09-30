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
 *  @version $Id: OSXStackRFCOMMServer.h 2416 2008-10-09 17:59:55Z skarzhevskyy $
 */

#import "OSXStackRFCOMM.h"
#import "OSXStackSDPServer.h"

#import <IOBluetooth/objc/IOBluetoothSDPServiceRecord.h>
#import <IOBluetooth/objc/IOBluetoothSDPUUID.h>

class RFCOMMServerController : public ServerController {
public:

    BluetoothRFCOMMChannelID rfcommChannelID;

    RFCOMMChannelController* acceptClientComm;

    NSMutableDictionary* rfcommChannelIDDataElement;

    jboolean authenticate;

public:
    RFCOMMServerController();
    virtual ~RFCOMMServerController();

    virtual IOReturn updateSDPServiceRecord();
    IOReturn publish();
    void close();
};

class RFCOMMServicePublish: public Runnable {
public:
    jbyte* uuidValue;
    int uuidValueLength;
    jboolean obexSrv;
    jboolean authenticate;
    jboolean encrypt;
    const jchar *serviceName;
    int serviceNameLength;

    RFCOMMServerController* comm;
    volatile IOReturn status;

    RFCOMMServicePublish();
    virtual void run();
};