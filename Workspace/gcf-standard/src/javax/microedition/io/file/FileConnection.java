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

package javax.microedition.io.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.StreamConnection;

public interface FileConnection extends StreamConnection {
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws ConnectionClosedException
     */
    long availableSize();
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws ConnectionClosedException
     */
    boolean canRead();
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws ConnectionClosedException
     */
    boolean canWrite();
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws ConnectionClosedException
     */
    void create() throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws ConnectionClosedException
     */
    void delete() throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws ConnectionClosedException
     */
    long directorySize(boolean includeSubDirs) throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws ConnectionClosedException
     */
    boolean exists();
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws ConnectionClosedException
     */
    long fileSize() throws IOException;
    
    String getName();
    
    String getPath();
    
    String getURL();
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws ConnectionClosedException
     */
    boolean isDirectory();
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws ConnectionClosedException
     */
    boolean isHidden();
    
    boolean isOpen();
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws ConnectionClosedException
     */
    long lastModified();
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws ConnectionClosedException
     */
    Enumeration list() throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws ConnectionClosedException
     */
    Enumeration list(String filter, boolean includeHidden) throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws ConnectionClosedException
     */
    void mkdir() throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     */
    DataInputStream openDataInputStream() throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     */
    DataOutputStream openDataOutputStream() throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     */
    InputStream openInputStream() throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     */
    OutputStream openOutputStream() throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws IllegalArgumentException
     */
    OutputStream openOutputStream(long byteOffset) throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws ConnectionClosedException
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
    void rename(String newName) throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IOException
     * @throws ConnectionClosedException
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
    void setFileConnection(String fileName) throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws ConnectionClosedException
     */
    void setHidden(boolean hidden) throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws ConnectionClosedException
     */
    void setReadable(boolean readable) throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws ConnectionClosedException
     */
    void setWritable(boolean writable) throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws ConnectionClosedException
     */
    long totalSize();
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws IOException
     * @throws ConnectionClosedException
     * @throws IllegalArgumentException
     */
    void truncate(long byteOffset) throws IOException;
    
    /**
     * @throws SecurityException
     * @throws IllegalModeException
     * @throws ConnectionClosedException
     */
    long usedSize();
}
