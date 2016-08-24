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

import java.io.IOException;

public interface SocketConnection extends StreamConnection {
    byte DELAY= 0;
    byte LINGER= 1;
    byte KEEPALIVE= 2;
    byte RCVBUF= 3;
    byte SNDBUF= 4;

    /**
     * @throws IOException
     */
    String getAddress() throws IOException;

    /**
     * @throws IOException
     */
    String getLocalAddress() throws IOException;
    
    /**
     * @throws IOException
     */
    int getLocalPort() throws IOException;
    
    /**
     * @throws IOException
     */
    int getPort() throws IOException;
    
    /**
     * @throws IllegalArgumentException
     * @throws IOException
     */
    int getSocketOption(byte option) throws IllegalArgumentException, IOException;

    /**
     * @throws IllegalArgumentException
     * @throws IOException
     */
    void setSocketOption(byte option, int value) throws IllegalArgumentException, IOException;
}
