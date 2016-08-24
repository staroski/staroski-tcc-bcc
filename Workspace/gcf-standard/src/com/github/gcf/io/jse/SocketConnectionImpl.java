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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.microedition.io.SocketConnection;
import javax.net.SocketFactory;

import com.github.gcf.io.AbstractConnection;

/**
 * @author Marcel Patzlaff
 */
class SocketConnectionImpl extends AbstractConnection implements SocketConnection {
    protected final Socket socket;
    
    protected SocketConnectionImpl(Socket socket) {
        this.socket= socket;
    }
    
    public SocketConnectionImpl(String host, int port) throws IOException {
        this(SocketFactory.getDefault().createSocket(host, port));
    }
    
    public final String getAddress() throws IOException {
        ensureOpen();
        return socket.getInetAddress().getHostAddress();
    }

    public final String getLocalAddress() throws IOException {
        ensureOpen();
        return socket.getLocalAddress().getHostAddress();
    }

    public final int getLocalPort() throws IOException {
        ensureOpen();
        return socket.getLocalPort();
    }

    public final int getPort() throws IOException {
        ensureOpen();
        return socket.getPort();
    }

    public final int getSocketOption(byte option) throws IllegalArgumentException, IOException {
        ensureOpen();
        switch(option) {
            case DELAY: {
                return socket.getTcpNoDelay() ? 1 : 0;
            }
            
            case KEEPALIVE: {
                return socket.getKeepAlive() ? 1 : 0;
            }
            
            case LINGER: {
                return socket.getSoLinger();
            }
            
            case RCVBUF: {
                return socket.getReceiveBufferSize();
            }
            
            case SNDBUF: {
                return socket.getSendBufferSize();
            }
            
            default: {
                throw new IllegalArgumentException("unvalid socket option: " + option);
            }
        }
    }

    public final void setSocketOption(byte option, int value) throws IllegalArgumentException, IOException {
        ensureOpen();
        switch(option) {
            case DELAY: {
                socket.setTcpNoDelay(value != 0);
            }
            
            case KEEPALIVE: {
                socket.setKeepAlive(value != 0);
            }
            
            case LINGER: {
                socket.setSoLinger(value != 0, value);
            }
            
            case RCVBUF: {
                socket.setReceiveBufferSize(value);
            }
            
            case SNDBUF: {
                socket.setSendBufferSize(value);
            }
            
            default: {
                throw new IllegalArgumentException("unvalid socket option: " + option);
            }
        }
    }

    public final DataInputStream openDataInputStream() throws IOException {
        ensureOpen();
        return new DataInputStream(openInputStream());
    }

    public final InputStream openInputStream() throws IOException {
        ensureOpen();
        return new RegisteredInputStream(socket.getInputStream());
    }

    protected void closeMainResource() throws IOException {
        socket.close();
    }

    public final DataOutputStream openDataOutputStream() throws IOException {
        ensureOpen();
        return new DataOutputStream(openOutputStream());
    }

    public final OutputStream openOutputStream() throws IOException {
        ensureOpen();
        return new RegisteredOutputStream(socket.getOutputStream());
    }
}
