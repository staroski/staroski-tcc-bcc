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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SecurityInfo;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


/**
 * @author Marcel Patzlaff
 */
final class HttpsConnectionImpl extends HttpConnectionImpl implements HttpsConnection {
    private final class InterceptingSSLSocketFactory extends SSLSocketFactory {
        private final SSLSocketFactory _factory;
        
        protected InterceptingSSLSocketFactory(SSLSocketFactory factory) {
            _factory= factory;
        }
        
        public Socket createSocket() throws IOException {
            underlyingSocket= (SSLSocket) _factory.createSocket();
            return underlyingSocket;
        }

        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            underlyingSocket= (SSLSocket) _factory.createSocket(address, port, localAddress, localPort);
            return underlyingSocket;
        }

        public Socket createSocket(InetAddress host, int port) throws IOException {
            underlyingSocket= (SSLSocket) _factory.createSocket(host, port);
            return underlyingSocket;
        }

        public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            underlyingSocket= (SSLSocket) _factory.createSocket(s, host, port, autoClose);
            return underlyingSocket;
        }

        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            underlyingSocket= (SSLSocket) _factory.createSocket(host, port, localHost, localPort);
            return underlyingSocket;
        }

        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            underlyingSocket= (SSLSocket) _factory.createSocket(host, port);
            return underlyingSocket;
        }

        public String[] getDefaultCipherSuites() {
            return _factory.getDefaultCipherSuites();
        }

        public String[] getSupportedCipherSuites() {
            return _factory.getSupportedCipherSuites();
        }
    }
    
    protected SSLSocket underlyingSocket;
    private SecurityInfo _securityInfo;
    
    public HttpsConnectionImpl(HttpsURLConnection connection) {
        super(connection);
        
        connection.setSSLSocketFactory(new InterceptingSSLSocketFactory(connection.getSSLSocketFactory()));
    }
    
    public SecurityInfo getSecurityInfo() throws IOException {
        ensureOpen();
        if(underlyingSocket == null) {
            connection.connect();
        }
        
        if(_securityInfo == null) {
            _securityInfo= SSLSecurityInfoImpl.create(underlyingSocket);
        }
        
        return _securityInfo;
    }
}
