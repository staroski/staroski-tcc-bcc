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
 *  @version $Id: LocalServerSocket.java 2960 2009-03-24 19:22:32Z skarzhevskyy $
 */
package org.bluecove.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import org.bluecove.socket.LocalSocketImpl.LocalSocketOptions;

/**
 * Unix domain server socket on Linux.
 * 
 * Inheritance from java.net.ServerSocket is mainly for documentation consistency. 
 */
public class LocalServerSocket extends java.net.ServerSocket {
    
    /**
     * The implementation of this Socket.
     */
    private LocalSocketImpl impl;
    
    public LocalServerSocket() throws IOException {
        super();
        impl = new LocalSocketImpl();
        impl.create(true);
    }
    
    public LocalServerSocket(SocketAddress endpoint) throws IOException {
        this();
        bind(endpoint);
    }
    
    public LocalServerSocket(SocketAddress endpoint, int backlog) throws IOException {
        this();
        bind(endpoint, backlog);
    }
    
    @Override
    public void bind(SocketAddress endpoint, int backlog) throws IOException {
        impl.bind(endpoint);
        impl.listen(backlog);
    }
    
    @Override
    public Socket accept() throws IOException {
        LocalSocketImpl clientImpl = new LocalSocketImpl();
        impl.accept(clientImpl);
        return new LocalSocket(clientImpl);
    }
    
    @Override
    public SocketAddress getLocalSocketAddress() {
        return impl.getSocketAddress();
    }
	
    public void close() throws IOException {
        SocketAddress endpoint = getLocalSocketAddress();
		impl.close();
		if ((endpoint != null) && !((LocalSocketAddress)endpoint).isAbstractNamespace()) {
		    impl.unlink(((LocalSocketAddress)endpoint).getName());
		}
	}
	
    @Override
    public boolean isBound() {
    	return impl.isBound();
    }
    
    @Override
    public boolean isClosed() {
    	return impl.isClosed();
    }
    
    @Override
    public String toString() {
        if (isBound()) {
            return "LocalServerSocket[" + getLocalSocketAddress() + "]";
        } else {
            return "LocalServerSocket[unbound]";
        }
    }

    
    /**
     * Enable/disable the SO_PASSCRED socket option.
     * 
     * @param on
     * @throws SocketException 
     */
    public void setReceiveCredentials(boolean on) throws SocketException  {
        if (isClosed()) {
            throw new SocketException("Socket is already closed");
        }
        impl.setOption(LocalSocketOptions.SO_PASSCRED, Integer.valueOf((on?1:0)));
    }
    
    public boolean getReceiveCredentials() throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is already closed");
        }
        Object value = impl.getOption(LocalSocketOptions.SO_PASSCRED);
        if (value instanceof Integer) {
            return (((Integer) value).intValue() > 0);
        } else {
            return false;
        }
    }
}
