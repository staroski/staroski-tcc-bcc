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
import java.net.ServerSocket;

import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import javax.net.ServerSocketFactory;

import com.github.gcf.io.AbstractConnection;

/**
 * @author Marcel Patzlaff
 */
class ServerSocketConnectionImpl extends AbstractConnection implements ServerSocketConnection {
    protected final ServerSocket serverSocket;

    protected ServerSocketConnectionImpl(ServerSocket serverSocket) {
        this.serverSocket= serverSocket;
    }
    
    public ServerSocketConnectionImpl(int port) throws IOException {
        this(ServerSocketFactory.getDefault().createServerSocket(port));
    }
    
    public final String getLocalAddress() throws IOException {
        ensureOpen();
        return serverSocket.getInetAddress().getHostAddress();
    }

    public final int getLocalPort() throws IOException {
        ensureOpen();
        return serverSocket.getLocalPort();
    }

    public StreamConnection acceptAndOpen() throws IOException {
        ensureOpen();
        return new SocketConnectionImpl(serverSocket.accept());
    }

    protected void closeMainResource() throws IOException {
        serverSocket.close();
    }
}
