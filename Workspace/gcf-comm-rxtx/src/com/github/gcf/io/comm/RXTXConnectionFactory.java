/*
 * GCF - Generic Connection Framework for Java SE
 *       GCF-Comm (RXTX)
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

/*
 * $Id: RXTXConnectionFactory.java 26791 2010-02-25 16:09:07Z marcel $
 */
package com.github.gcf.io.comm;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.microedition.io.Connection;

import com.github.gcf.io.IConnectionFactory;
import com.github.gcf.io.PrimitiveURI;


/**
 * @author Marcel Patzlaff
 * @version $Revision: 26791 $
 */
public final class RXTXConnectionFactory implements IConnectionFactory {
    public boolean conflictsWith(String protocol, IConnectionFactory factory) {
        return false;
    }

    public Set getSupportedProtocols() {
        HashSet protocols= new HashSet();
        protocols.add("comm");
        return protocols;
    }

    public Connection openPrim(String protocol, String uriStr, int mode, boolean timeouts) throws IOException {
        PrimitiveURI uri= new PrimitiveURI(uriStr);
        if(uri.path == null) {
            throw new IllegalArgumentException("port has to be specified");
        }
        
        StringBuffer args= new StringBuffer(uri.path);
        String sc= ";";
        // first part is the port name
        int scIndex= args.indexOf(sc);
        String portName;
        
        if(scIndex > 0) {
            portName= args.substring(0, scIndex);
            args.delete(0, scIndex + 1);
        } else {
            portName= args.toString();
            args.setLength(0);
        }
        
        // default values
        Integer baudrate= null;
        int bitsperchar= SerialPort.DATABITS_8;
        int stopbits= SerialPort.STOPBITS_1;
        int parity= SerialPort.PARITY_NONE;
        boolean blocking= true;
        boolean autocts= true;
        boolean autorts= true;
        
        String eq= "=";
        // parse parameters
        while(args.length() > 0) {
            int eqIndex= args.indexOf(eq);
            
            if(eqIndex <= 0) {
                throw new IOException("Invalid option list comm:" + uriStr);
            }
            
            String argName= args.substring(0, eqIndex);
            String argValue;
            scIndex= args.indexOf(sc);
            if(scIndex < 0) {
                argValue= args.toString();
                args.setLength(0);
            } else {
                argValue= args.substring(eqIndex + 1, scIndex);
                args.delete(0, scIndex + 1);
            }
            try {
                if(argName.equals("baudrate")) {
                    baudrate= Integer.valueOf(argValue);
                } else if(argName.equals("bitsperchar")) {
                    bitsperchar= parseBitsPerChar(argValue);
                } else if(argName.equals("stopbits")) {
                    stopbits= parseStopBits(argValue);
                } else if(argName.equals("parity")) {
                    parity= parseParity(argValue);
                } else if(argName.equals("blocking")) {
                    blocking= parseSwitchValue(argValue);
                } else if(argName.equals("autocts")) {
                    autocts= parseSwitchValue(argValue);
                } else if(argName.equals("autorts")) {
                    autorts= parseSwitchValue(argValue);
                } else {
                    throw new IOException("Unsupported option: " + argName);
                }
            } catch (RuntimeException re) {
                throw new IllegalArgumentException("Invalid value for option " + argName, re);
            }
        }
        
        try {
            SerialPort port= (SerialPort) CommPortIdentifier.getPortIdentifier(portName).open(
                Thread.currentThread().getName(),
                5000
            );
            port.setSerialPortParams(
                baudrate == null ? port.getBaudRate() : baudrate.intValue(),
                bitsperchar,
                stopbits,
                parity
            );
            
            if(blocking || !timeouts) {
                port.disableReceiveTimeout();
            }
            port.setDTR(autocts);
            port.setRTS(autorts);
            return new RXTXCommConnectionImpl(port);
        } catch (Exception e) {
            throw new IOException("Could not open connection", e);
        }
    }
    
    private static int parseBitsPerChar(String value) {
        if(value.equals("7")) {
            return SerialPort.DATABITS_7;
        } else if(value.equals("8")) {
            return SerialPort.DATABITS_8;
        } else {
            throw new IllegalArgumentException("(7|8) expected");
        }
    }
    
    private static int parseParity(String value) {
        if(value.equals("odd")) {
            return SerialPort.PARITY_ODD;
        } else if(value.equals("even")) {
            return SerialPort.PARITY_EVEN;
        } else if(value.equals("none")) {
            return SerialPort.PARITY_NONE;
        } else {
            throw new IllegalArgumentException("(odd|even|none) expected");
        }
    }
    
    private static int parseStopBits(String value) {
        if(value.equals("1")) {
            return SerialPort.STOPBITS_1;
        } else if(value.equals("2")) {
            return SerialPort.STOPBITS_2;
        } else {
            throw new IllegalArgumentException("(1|2) expected");
        }
    }
    
    private static boolean parseSwitchValue(String value) {
        if(value.equals("on")) {
            return true;
        } else if(value.equals("off")) {
            return false;
        }
        
        throw new IllegalArgumentException("(on|off) expected");
    }
}
