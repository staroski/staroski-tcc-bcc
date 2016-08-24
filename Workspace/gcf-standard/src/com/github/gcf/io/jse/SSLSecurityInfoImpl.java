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
import java.security.cert.X509Certificate;

import javax.microedition.io.SecurityInfo;
import javax.microedition.pki.Certificate;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * @author Marcel Patzlaff
 */
final class SSLSecurityInfoImpl implements SecurityInfo {
    static SSLSecurityInfoImpl create(SSLSocket socket) throws IOException {
        SSLSession session= socket.getSession();
        
        java.security.cert.Certificate[] certs= session.getPeerCertificates();
        CertificateImpl ci= null;
        if(certs != null && certs.length > 0 && certs[0] instanceof X509Certificate) {
            // TODO: is this the right way?
            ci= new CertificateImpl((X509Certificate) certs[0]);
        }
        
        // parse protocol name and version
        final String protocol= session.getProtocol();
        String protocolName= null;
        String protocolVersion= null;
        if(protocol.startsWith("TLS")) {
            protocolName= "TLS";
            protocolVersion= "3.1";
        } else if(protocol.startsWith("SSL")) {
            protocolName= "SSL";
            protocolVersion= "3.0";
        }
        
        
        return new SSLSecurityInfoImpl(
            session.getCipherSuite(),
            protocolName,
            protocolVersion,
            ci
        );
    }

    private final String _cypherSuite;
    private final String _protocolName;
    private final String _protocolVersion;
    private final Certificate _serverCert;
    
    private SSLSecurityInfoImpl(String cs, String pn, String pv, Certificate sc) {
        _cypherSuite= cs;
        _protocolName= pn;
        _protocolVersion= pv;
        _serverCert= sc;
    }
    
    public String getCipherSuite() {
        return _cypherSuite;
    }

    public String getProtocolName() {
        return _protocolName;
    }

    public String getProtocolVersion() {
        return _protocolVersion;
    }

    public Certificate getServerCertificate() {
        return _serverCert;
    }
}
