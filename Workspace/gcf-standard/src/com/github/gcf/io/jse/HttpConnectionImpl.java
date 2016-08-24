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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import javax.microedition.io.HttpConnection;

import com.github.gcf.io.AbstractConnection;

/**
 * @author Marcel Patzlaff
 */
class HttpConnectionImpl extends AbstractConnection implements HttpConnection {
    protected final HttpURLConnection connection;
    
    public HttpConnectionImpl(HttpURLConnection connection) {
        this.connection= connection;
    }
    
    public final long getDate() throws IOException {
        ensureOpen();
        return connection.getDate();
    }

    public final long getExpiration() throws IOException {
        ensureOpen();
        return connection.getExpiration();
    }

    public final String getFile() {
        return connection.getURL().getFile();
    }

    public final String getHeaderField(String name) throws IOException {
        ensureOpen();
        return connection.getHeaderField(name);
    }

    public final String getHeaderField(int n) throws IOException {
        ensureOpen();
        return connection.getHeaderField(n);
    }

    public final long getHeaderFieldDate(String name, long def) throws IOException {
        ensureOpen();
        return connection.getHeaderFieldDate(name, def);
    }

    public final int getHeaderFieldInt(String name, int def) throws IOException {
        ensureOpen();
        return connection.getHeaderFieldInt(name, def);
    }

    public final String getHeaderFieldKey(int n) throws IOException {
        ensureOpen();
        return connection.getHeaderFieldKey(n);
    }

    public final String getHost() {
        return connection.getURL().getHost();
    }

    public final long getLastModified() throws IOException {
        ensureOpen();
        return connection.getLastModified();
    }

    public final int getPort() {
        return connection.getURL().getPort();
    }

    public final String getProtocol() {
        return connection.getURL().getProtocol();
    }

    public final String getQuery() {
        return connection.getURL().getQuery();
    }

    public final String getRef() {
        return connection.getURL().getRef();
    }

    public final String getRequestMethod() {
        return connection.getRequestMethod();
    }

    public final String getRequestProperty(String key) {
        return connection.getRequestProperty(key);
    }

    public final int getResponseCode() throws IOException {
        ensureOpen();
        return connection.getResponseCode();
    }

    public final String getResponseMessage() throws IOException {
        ensureOpen();
        return connection.getResponseMessage();
    }

    public final String getURL() {
        return connection.getURL().toString();
    }

    public final void setRequestMethod(String method) throws IOException {
        ensureOpen();
        connection.setRequestMethod(method);
    }

    public final void setRequestProperty(String key, String value) throws IOException {
        ensureOpen();
        connection.addRequestProperty(key, value);
    }

    public final String getEncoding() {
        return connection.getContentEncoding();
    }

    public final long getLength() {
        return connection.getContentLength();
    }

    public final String getType() {
        return connection.getContentType();
    }

    public DataInputStream openDataInputStream() throws IOException {
        ensureOpen();
        return new DataInputStream(openInputStream());
    }

    public InputStream openInputStream() throws IOException {
        ensureOpen();
        return new RegisteredInputStream(connection.getInputStream());
    }

    protected void closeMainResource() throws IOException {
        connection.disconnect();
    }

    public DataOutputStream openDataOutputStream() throws IOException {
        ensureOpen();
        return new DataOutputStream(openOutputStream());
    }

    public OutputStream openOutputStream() throws IOException {
        ensureOpen();
        return new RegisteredOutputStream(connection.getOutputStream());
    }
}
