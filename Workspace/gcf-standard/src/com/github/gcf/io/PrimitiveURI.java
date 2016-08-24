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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Marcel Patzlaff
 */
public class PrimitiveURI {
    public final String scheme;
    public final String userInfo;
    public final String host;
    public final int port;
    public final String path;
    public final String query;
    public final String fragment;
    public final String rawString;
    
    public PrimitiveURI(String uri) throws IllegalArgumentException {
        rawString= uri;
        
        int pos= 0;
        int at= uri.indexOf('@');
        int slash= uri.indexOf('/');
        int colon= uri.indexOf(':');
        
        if(colon < 0 || (at > 0 && colon > at) || (slash > 0 && colon > slash)) {
            throw new IllegalArgumentException("Invalid URI syntax: " + uri);
        }
        
        scheme= uri.substring(pos, colon);
        pos= colon + 1;

        if(slash == pos && uri.charAt(pos + 1) == '/') {
            pos+= 2;
            slash= uri.indexOf('/', pos);
        }
        
        if(at >= pos && (slash < 0 || at < slash)) {
            userInfo= uri.substring(pos, at);
            pos= at + 1;
        } else {
            userInfo= null;
        }
        
        if(uri.charAt(pos) == '/') {
            host= null;
            port= -1;
        } else {
            colon= uri.indexOf(':', pos);
            
            if(colon < 0 || (slash > 0 && colon > slash)) {
                host= uri.substring(pos, slash < 0 ? uri.length() : slash);
                port= -1;
            } else {
                host= colon == pos ? null : uri.substring(pos, colon);
                port= Integer.parseInt(uri.substring(colon + 1, slash < 0 ? uri.length() : slash));
            }
            
            pos= slash < 0 ? uri.length() : slash;
        }
        
        int qmark= uri.indexOf('?', pos);
        int hash= uri.indexOf('#', pos);
        
        if(pos < uri.length()) {
            int end= uri.length();
            if(hash > 0) {
                fragment= uri.substring(hash + 1);
                end= hash;
            } else {
                fragment= null;
            }
            
            if(qmark > 0) {
                query= uri.substring(qmark + 1, end);
                end= qmark;
            } else {
                query= null;
            }
            
            if(slash == pos) {
                path= uri.substring(slash, end);
            } else {
                path= null;
            }
        } else {
            path= null;
            query= null;
            fragment= null;
        }
    }
    
    public URL toURL() throws MalformedURLException {
        return new URL(rawString);
    }

    public String toString() {
        StringBuffer result= new StringBuffer();
        result.append("scheme = ").append(String.valueOf(scheme)).append(",\n");
        result.append("userInfo = ").append(String.valueOf(userInfo)).append(",\n");
        result.append("host = ").append(String.valueOf(host)).append(",\n");
        result.append("port = ").append(String.valueOf(port)).append(",\n");
        result.append("path = ").append(String.valueOf(path)).append(",\n");
        result.append("query = ").append(String.valueOf(query)).append(",\n");
        result.append("fragment = ").append(String.valueOf(fragment)).append(",\n");
        return result.toString();
    }
}
