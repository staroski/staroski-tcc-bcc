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

package com.github.gcf.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.microedition.io.Connection;

/**
 * @author Marcel Patzlaff
 */
public abstract class AbstractConnection implements Connection {
    protected final class RegisteredInputStream extends InputStream {
        private final InputStream _target;
        
        public RegisteredInputStream(InputStream target) {
            _target= target;
            resourceAcquired(this);
        }

        public int available() throws IOException {
            return _target.available();
        }

        public void close() throws IOException {
            try {
                _target.close();
            } finally {
                resourceReleased(this);
            }
        }

        public void mark(int readlimit) {
            _target.mark(readlimit);
        }

        public boolean markSupported() {
            return _target.markSupported();
        }

        public int read() throws IOException {
            return _target.read();
        }

        public int read(byte[] b, int off, int len) throws IOException {
            return _target.read(b, off, len);
        }

        public int read(byte[] b) throws IOException {
            return _target.read(b);
        }

        public void reset() throws IOException {
            _target.reset();
        }

        public long skip(long n) throws IOException {
            return _target.skip(n);
        }
    }
    
    protected final class RegisteredOutputStream extends OutputStream {
        private final OutputStream _target;
        
        public RegisteredOutputStream(OutputStream target) {
            _target= target;
            resourceAcquired(this);
        }

        public void close() throws IOException {
            try {
                _target.close();
            } finally {
                resourceReleased(this);
            }
        }

        public void flush() throws IOException {
            _target.flush();
        }

        public void write(byte[] b, int off, int len) throws IOException {
            _target.write(b, off, len);
        }

        public void write(byte[] b) throws IOException {
            _target.write(b);
        }

        public void write(int b) throws IOException {
            _target.write(b);
        }
    }
    
    protected boolean closed= false;
    private final ArrayList _registeredResources= new ArrayList();
    
    public final void close() throws IOException {
        if(closed) {
            return;
        }
        
        closed= true;
        
        if(_registeredResources.size() <= 0) {
            closeMainResource();
        }
    }
    
    protected void ensureOpen() throws IOException {
        if(closed) {
            throw new IOException("Connection is closed");
        }
    }
    
    protected final void resourceAcquired(Object resource) {
        _registeredResources.add(resource);
    }
    
    protected final void resourceReleased(Object resource) throws IOException {
        _registeredResources.remove(resource);
        
        if(closed && _registeredResources.size() <= 0) {
            closeMainResource();
        }
    }
    
    protected void closeMainResource() throws IOException {}
}
