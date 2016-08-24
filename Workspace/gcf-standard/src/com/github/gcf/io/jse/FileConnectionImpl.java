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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.ConnectionClosedException;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.IllegalModeException;

import com.github.gcf.io.AbstractConnection;


/**
 * @author Marcel Patzlaff
 */
final class FileConnectionImpl extends AbstractConnection implements FileConnection {
    private File _file;
    private final int _mode;

    public FileConnectionImpl(int mode, File file) {
        _mode= mode;
        _file= file;
    }
    
    public long availableSize() {
        ensureReadMode();
        return _file.getFreeSpace();
    }

    public boolean canRead() {
        ensureReadMode();
        return _file.canRead();
    }

    public boolean canWrite() {
        ensureReadMode();
        return _file.canWrite();
    }

    public void create() throws IOException {
        ensureWriteMode();
        _file.createNewFile();
    }

    public void delete() throws IOException {
        ensureWriteMode();
        
        if(!_file.exists()) {
            throw new IOException("target does not exist");
        }
        
        boolean deleted= _file.delete();
        
        if(_file.isDirectory() && !deleted) {
            throw new IOException("directory is not empty");
        }
    }

    public long directorySize(boolean includeSubDirs) throws IOException {
        ensureReadMode();
        
        // TODO
        return 0;
    }

    public boolean exists() {
        ensureReadMode();
        return _file.exists();
    }

    public long fileSize() throws IOException {
        ensureReadMode();
        
        if(!_file.exists()) {
            return -1L;
        }
        
        if(_file.isDirectory()) {
            throw new IOException("target is a directory");
        }
        
        return _file.length();
    }

    public String getName() {
        return _file.getName();
    }

    public String getPath() {
        return _file.getPath();
    }

    public String getURL() {
        return "file://" +_file.getAbsolutePath();
    }

    public boolean isDirectory() {
        ensureReadMode();
        return _file.isDirectory();
    }

    public boolean isHidden() {
        ensureReadMode();
        return _file.isHidden();
    }

    public boolean isOpen() {
        return !closed;
    }

    public long lastModified() {
        ensureReadMode();
        return _file.lastModified();
    }

    public Enumeration list() throws IOException {
        ensureReadMode();
        
        if(!_file.exists()) {
            throw new IOException("directory does not exist");
        }
        
        if(_file.isFile()) {
            throw new IOException("target is a file");
        }
        
        File[] files= _file.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if(pathname.isHidden()) {
                    return false;
                }
                
                String fileName= pathname.getName();
                
                if(fileName.equals(".") || fileName.equals("..")) {
                    return false;
                }
                
                return false;
            }
        });
        
        return new FileEnumerator(files, false);
    }

    public Enumeration list(final String filter, final boolean includeHidden) throws IOException {
        ensureReadMode();
        
        if(filter == null) {
            // hate this exception here but it is required according to javadoc...
            throw new NullPointerException("filter");
        }
        
        // TODO Auto-generated method stub
        return null;
    }

    public void mkdir() throws IOException {
        ensureWriteMode();
        
        if(_file.exists()) {
            throw new IOException("target already exists");
        }
        
        if(!_file.mkdir()) {
            throw new IOException("directory could not be created");
        }
    }

    public DataInputStream openDataInputStream() throws IOException {
        InputStream in= openInputStream();
        return new DataInputStream(in);
    }

    public DataOutputStream openDataOutputStream() throws IOException {
        OutputStream out= openOutputStream();
        return new DataOutputStream(out);
    }

    public InputStream openInputStream() throws IOException {
        ensureReadMode();
        return new FileInputStream(_file);
    }

    public OutputStream openOutputStream() throws IOException {
        ensureWriteMode();
        return new FileOutputStream(_file);
    }

    public OutputStream openOutputStream(long byteOffset) throws IOException {
        ensureWriteMode();
        // TODO Auto-generated method stub
        return null;
    }

    public void rename(String newName) throws IOException {
        ensureWriteMode();
        
        if(newName == null) {
            // hate this exception here but it is required according to javadoc...
            throw new NullPointerException("newName");
        }
        
        // TODO
    }

    public void setFileConnection(String fileName) throws IOException {
        ensureOpen();
        
        if(fileName == null) {
            // hate this exception here but it is required according to javadoc...
            throw new NullPointerException("fileName");
        }
        
        // FIXME: is this right?
        if(fileName.indexOf(File.separatorChar) >= 0) {
            throw new IOException("fileName contains path specification");
        }
        
        File newFile;
        
        if(fileName.equals("..")) {
            newFile= _file.getParentFile();
        } else {
            newFile= new File(_file, fileName);
        }
        
        if(newFile == null || !newFile.exists()) {
            throw new IOException("file does not exist");
        }
        
        _file= newFile;
    }

    public void setHidden(boolean hidden) throws IOException {
        throw new IOException("unsupported operation");
    }

    public void setReadable(boolean readable) throws IOException {
        ensureWriteMode();
        
        if(!_file.exists()) {
            throw new IOException("file does not exist");
        }
        
        _file.setReadable(readable);
    }

    public void setWritable(boolean writable) throws IOException {
        ensureWriteMode();
        
        if(!_file.exists()) {
            throw new IOException("file does not exist");
        }
        
        _file.setWritable(writable);
    }

    public long totalSize() {
        ensureReadMode();
        long size= _file.getTotalSpace();
        return size == 0 ? -1L : size;
    }

    public void truncate(long byteOffset) throws IOException {
        ensureWriteMode();
        
        if(!_file.exists()) {
            throw new IOException("target does not exist");
        }
        
        if(_file.isDirectory()) {
            throw new IOException("target is a directory");
        }
        
        // TODO
        
    }

    public long usedSize() {
        ensureReadMode();
        long diff= _file.getTotalSpace() - _file.getFreeSpace();
        return diff >= 0 ? diff : 0L;
    }

    protected void ensureOpen() throws ConnectionClosedException {
        if(closed) {
            throw new ConnectionClosedException();
        }
    }
    
    private void ensureWriteMode() throws ConnectionClosedException, IllegalModeException {
        ensureOpen();
        if((_mode & Connector.WRITE) == 0) {
            throw new IllegalModeException("not in write mode");
        }
    }
    
    private void ensureReadMode() throws ConnectionClosedException, IllegalModeException {
        ensureOpen();
        if((_mode & Connector.READ) == 0) {
            throw new IllegalModeException("not in read mode");
        }
    }
}