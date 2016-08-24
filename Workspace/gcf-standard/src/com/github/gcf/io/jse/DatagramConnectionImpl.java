/*
 * GCF - Generic Connection Framework for Java SE
 *       GCF-Standard
 *
 * Copyright (c) 2007-2011 Marcel Patzlaff (marcel.patzlaff@gmail.com)
 *
 * This library is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.gcf.io.jse;

import java.io.IOException;
import java.net.DatagramSocket;

import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

import com.github.gcf.io.AbstractConnection;
import com.github.gcf.io.PrimitiveURI;


/**
 * @author Marcel Patzlaff
 */
class DatagramConnectionImpl extends AbstractConnection implements DatagramConnection {
    protected final DatagramSocket socket;
    private final PrimitiveURI _defaultAddress;
    
    private final int _maximumSize;
    
    protected DatagramConnectionImpl(PrimitiveURI uri, boolean serverMode) throws IOException {
        if(serverMode) {
            socket= new DatagramSocket(uri.port);
            _defaultAddress= null;
        } else {
            socket= new DatagramSocket();
            _defaultAddress= uri;
        }
        
        int sendBuf= socket.getSendBufferSize();
        int recvBuf= socket.getReceiveBufferSize();
        _maximumSize= sendBuf > recvBuf ? recvBuf : sendBuf;
    }
    
    public final int getMaximumLength() throws IOException {
        ensureOpen();
        return _maximumSize;
    }

    public final int getNominalLength() throws IOException {
        ensureOpen();
        return _maximumSize;
    }

    public final Datagram newDatagram(byte[] buf, int size, String addr) throws IOException {
        ensureOpen();
        
        if(size > _maximumSize) {
            throw new IOException("requested size exceeds maximum");
        }
        
        Datagram datagram= new DatagramImpl(buf, size);
        datagram.setAddress(addr);
        return datagram;
    }

    public final Datagram newDatagram(byte[] buf, int size) throws IOException {
        ensureOpen();
        DatagramImpl datagram= new DatagramImpl(buf, size);
        
        if(_defaultAddress != null) {
            datagram.setAddress(_defaultAddress);
        }
        
        return datagram;
    }

    public final Datagram newDatagram(int size, String addr) throws IOException {
        return newDatagram(new byte[size], size, addr);
    }

    public final Datagram newDatagram(int size) throws IOException {
        return newDatagram(new byte[size], size);
    }

    public final void receive(Datagram dgram) throws IOException {
        if(_defaultAddress != null) {
            throw new IOException("Connection is in client mode");
        }
        
        if(!(dgram instanceof DatagramImpl)) {
            throw new IllegalArgumentException("Invalid Datagram");
        }
        
        ensureOpen();
        DatagramImpl d= (DatagramImpl) dgram;
        d.doReceive(socket);
    }

    public final void send(Datagram dgram) throws IOException {
        if(_defaultAddress == null) {
            throw new IOException("Connection is in server mode");
        }
        
        if(!(dgram instanceof DatagramImpl)) {
            throw new IllegalArgumentException("Invalid Datagram");
        }
        
        ensureOpen();
        DatagramImpl d= (DatagramImpl) dgram;
        d.doSend(socket);
    }
    
    protected void closeMainResource() throws IOException {
        socket.close();
    }
}
