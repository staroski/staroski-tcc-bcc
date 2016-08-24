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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import javax.microedition.io.file.FileSystemListener;

public final class FileSystemRegistryImpl {
    private final static long POLLING_INTERVAL= 2000L; 
    
    private final class CheckerTask extends Thread {
        private boolean _cancelled= false;
        
        public void run() {
            while(true) {
                synchronized (this) {
                    if(_cancelled) {
                        break;
                    }
                    
                    try {
                        this.wait(POLLING_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
                checkRoots();
            }
        }
        
        protected void cancel() {
            synchronized (this) {
                _cancelled= true;
                this.notify();
            }
        }
    }
    
    private final ArrayList _listeners= new ArrayList();
    private HashSet _oldRoots= null;
    private CheckerTask _checkerTask= null;
    
    public synchronized boolean addFileSystemListener(FileSystemListener listener) {
        if(!_listeners.contains(listener)) {
            _listeners.add(listener);
            if(_listeners.size() == 1) {
                startPolling();
            }
            
            return true;
        }
        
        return false;
    }
    
    public synchronized Enumeration listRoots() {
        return new FileEnumerator(File.listRoots(), true);
    }
    
    public synchronized boolean removeFileSystemListener(FileSystemListener listener) {
        if(_listeners.remove(listener)) {
            if(_listeners.size() <= 0) {
                stopPolling();
            }
            
            return true;
        }
        
        return false;
    }
    
    protected synchronized void checkRoots() {
        if(_oldRoots == null) {
            return;
        }
        
        File[] roots= File.listRoots();
        HashSet oldRoots= _oldRoots;
        HashSet newRoots= new HashSet();
        for(int i= 0; i < roots.length; ++i) {
            File cf= roots[i];
            
            if(!oldRoots.remove(cf)) {
                // new root
                notifyListeners(FileSystemListener.ROOT_ADDED, cf.getAbsolutePath());
            }
            
            newRoots.add(cf);
        }
        
        if(oldRoots.size() > 0) {
            // removed roots
            Iterator iter= oldRoots.iterator();
            while(iter.hasNext()) {
                File cf= (File) iter.next();
                notifyListeners(FileSystemListener.ROOT_REMOVED, cf.getAbsolutePath());
            }
        }
        
        _oldRoots= newRoots;
    }
    
    private void startPolling() {
        _oldRoots= new HashSet();
        File[] roots= File.listRoots();
        
        for(int i= 0; i < roots.length; ++i) {
            _oldRoots.add(roots[i]);
        }
        
        _checkerTask= new CheckerTask();
        _checkerTask.start();
    }
    
    private void stopPolling() {
        _checkerTask.cancel();
        _checkerTask= null;
        _oldRoots= null;
    }
    
    private void notifyListeners(int state, String rootName) {
        for(int i= 0; i < _listeners.size(); ++i) {
            FileSystemListener l= (FileSystemListener) _listeners.get(i);
            l.rootChanged(state, rootName);
        }
    }
}
