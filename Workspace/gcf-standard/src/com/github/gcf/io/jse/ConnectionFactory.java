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

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Set;

import javax.microedition.io.Connection;
import javax.net.ssl.HttpsURLConnection;

import com.github.gcf.io.IConnectionFactory;
import com.github.gcf.io.PrimitiveURI;


/**
 * @author Marcel Patzlaff
 */
public final class ConnectionFactory implements IConnectionFactory {
    public boolean conflictsWith(String protocol, IConnectionFactory factory) {
        return false;
    }

    public Set getSupportedProtocols() {
        HashSet protocols= new HashSet();
        protocols.add("file");
        protocols.add("datagram");
        protocols.add("socket");
        protocols.add("serversocket");
        protocols.add("ssl");
        protocols.add("http");
        protocols.add("https");
        return protocols;
    }

    public Connection openPrim(String protocol, String uriStr, int mode, boolean timeouts) throws IOException {
        PrimitiveURI uri= new PrimitiveURI(uriStr);
        
        // create HTTP connection
        if(protocol.equals("http")) {
            return new HttpConnectionImpl((HttpURLConnection) uri.toURL().openConnection());
        }
        
        // create HTTPS connection
        if(protocol.equals("https")) {
            return new HttpsConnectionImpl((HttpsURLConnection) uri.toURL().openConnection());
        }
        
        // create FILE connection
        if(protocol.equals("file")) {
            File file= new File(uri.path);
            return new FileConnectionImpl(mode, file);
        }
        
        if (uri.path != null || uri.query != null || uri.fragment != null) {
            throw new IllegalArgumentException("Malformed address");
        }

        if(uri.port < 0) {
            throw new IOException("Invalid port " + uri.rawString);
        }
        
        // create DATAGRAM connection
        if(protocol.equals("datagram")) {
            return new UDPDatagramConnectionImpl(uri, uri.host == null);
        }

        if(uri.host != null) {
            if(protocol.equals("ssl")) {
                return new SSLConnectionImpl(uri.host, uri.port);
            } else if(protocol.equals("socket")) {
                return new SocketConnectionImpl(uri.host, uri.port);
            } else {
                throw new IllegalArgumentException("Malformed address for server socket");
            }
        } else {
            if(protocol.equals("ssl")) {
                throw new IllegalArgumentException("SSL server unsupported");
            }
            
            return new ServerSocketConnectionImpl(uri.port);
        }
    }
}
