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

import java.security.cert.X509Certificate;

import javax.microedition.pki.Certificate;

/**
 * @author Marcel Patzlaff
 */
final class CertificateImpl implements Certificate {
    private final X509Certificate _cert;
    
    CertificateImpl(X509Certificate cert) {
        _cert= cert;
    }
    
    public String getIssuer() {
        return _cert.getIssuerX500Principal().getName();
    }

    public long getNotAfter() {
        return _cert.getNotAfter().getTime();
    }

    public long getNotBefore() {
        return _cert.getNotBefore().getTime();
    }

    public String getSerialNumber() {
        return _cert.getSerialNumber().toString();
    }

    public String getSigAlgName() {
        return _cert.getSigAlgName();
    }

    public String getSubject() {
        return _cert.getSubjectX500Principal().getName();
    }

    public String getType() {
        return _cert.getType();
    }

    public String getVersion() {
        return String.valueOf(_cert.getVersion());
    }
    
    public String toString() {
        return _cert.toString();
    }

}
