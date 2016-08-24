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

package javax.microedition.io;

/**
 * Valid URI scheme:
 * <pre>
 * Connector.open("comm:/dev/ttyS0;baudrate=19200;bitsperchar=8;stopbits=1;parity=none;blocking=on;autocts=on;autorts=on");
 * </pre>
 */
public interface CommConnection extends StreamConnection {
    int getBaudRate();
    int setBaudRate(int baudrate);
}
