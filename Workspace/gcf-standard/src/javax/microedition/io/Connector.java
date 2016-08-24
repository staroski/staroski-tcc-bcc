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

package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.gcf.io.FactoryRegistry;


public class Connector {
    public static final int READ= 0x01;
    public static final int WRITE= 0x02;
    public static final int READ_WRITE= READ | WRITE;
    
    /**
     * @throws IllegalArgumentException
     * @throws ConnectionNotFoundException
     * @throws IOException
     * @throws SecurityException
     */
    public static Connection open(String name) throws IOException {
        return open(name, READ_WRITE);
    }

    /**
     * @throws IllegalArgumentException
     * @throws ConnectionNotFoundException
     * @throws IOException
     * @throws SecurityException
     */
    public static Connection open(String name, int mode) throws IOException {
        return open(name, mode, false);
    }
    
    /**
     * @throws IllegalArgumentException
     * @throws ConnectionNotFoundException
     * @throws IOException
     * @throws SecurityException
     */
    public static Connection open(String name, int mode, boolean timeouts) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("URI must not be null");
        }

        int colon= name.indexOf(':');
        
        if(colon < 0) {
            throw new IllegalArgumentException("invalid uri scheme");
        }
        
        String scheme= name.substring(0, colon);
        return FactoryRegistry.openConnection(scheme, name, mode, timeouts);
    }
    
    /**
     * @throws IllegalArgumentException
     * @throws ConnectionNotFoundException
     * @throws IOException
     * @throws SecurityException
     */
    public static DataInputStream openDataInputStream(String name) throws IOException {
        Connection c= Connector.open(name, Connector.READ);
        
        try {
            if(!(c instanceof InputConnection)) {
                throw new IllegalArgumentException(name + " does not refer to an InputConnection");
            }
        
            return ((InputConnection) c).openDataInputStream();
        } finally {
            c.close();
        }
    }

    /**
     * @throws IllegalArgumentException
     * @throws ConnectionNotFoundException
     * @throws IOException
     * @throws SecurityException
     */
    public static DataOutputStream openDataOutputStream(String name) throws IOException {
        Connection c= Connector.open(name, Connector.WRITE);

        try {
            if(!(c instanceof OutputConnection)) {
                throw new IllegalArgumentException(name + " does not refer to an OutputConnection");
            }
        
            return ((OutputConnection) c).openDataOutputStream();
        } finally {
            c.close();
        }
    }

    /**
     * @throws IllegalArgumentException
     * @throws ConnectionNotFoundException
     * @throws IOException
     * @throws SecurityException
     */
    public static InputStream openInputStream(String name) throws IOException {
        return openDataInputStream(name);
    }

    /**
     * @throws IllegalArgumentException
     * @throws ConnectionNotFoundException
     * @throws IOException
     * @throws SecurityException
     */
    public static OutputStream openOutputStream(String name) throws IOException {
        return openDataOutputStream(name);
    }
    
    private Connector() {}
}
