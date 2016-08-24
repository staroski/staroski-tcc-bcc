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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import javax.microedition.io.Datagram;

import com.github.gcf.io.PrimitiveURI;


/**
 * Implements a UDP datagram for the UDP datagram connection.
 * 
 * @author Marcel Patzlaff
 */
final class DatagramImpl implements Datagram {
    public static final String DATAGRAM_SCHEME= "datagram://";
    
    public static PrimitiveURI validateDatagramAddress(String addr) throws IllegalArgumentException {
        PrimitiveURI uri;

        if (addr == null) {
            throw new IllegalArgumentException("Invalid address");
        }
        
        if(!(addr.startsWith(DATAGRAM_SCHEME))) {
            throw new IllegalArgumentException("Invalid scheme");
        }

        uri = new PrimitiveURI(addr);
        validateDatagramURI(uri);
        return uri;
    }
    
    private static void validateDatagramURI(PrimitiveURI uri) throws IllegalArgumentException {
        if (uri.path != null || uri.query != null || uri.fragment != null) {
            throw new IllegalArgumentException("Malformed address");
        }

        if (uri.port == -1) {
            throw new IllegalArgumentException("Missing port");
        }
    }
    
    
    private class DatagramInputStream extends InputStream {
        public int available() throws IOException {
            return length - (readWritePosition - offset);
        }

        public int read() throws IOException {
            if (readWritePosition >= length) {
                return -1;
            }

            return buffer[offset + readWritePosition++] & 0xFF;
        }

        public long skip(long n) throws IOException {
            if (n < 0) {
                return 0;
            }

            if (readWritePosition >= length) {
                return 0;
            }

            int min = Math.min((int) n, length - readWritePosition);
            readWritePosition += min;
            return min;
        }
    }
    
    private class DatagramOutputStream extends OutputStream {
        public void write(int ch) throws IOException {
            if (offset + readWritePosition >= buffer.length) {
                throw new IOException("Buffer full");
            }

            buffer[offset + readWritePosition++] = (byte)ch;
            length = readWritePosition;
        }
    }
    
    private InetSocketAddress _socketAddress;
    private String _address;
    private final DatagramPacket _internalPacket;
    
    private final DataInputStream _reader= new DataInputStream(new DatagramInputStream());
    private final DataOutputStream _writer= new DataOutputStream(new DatagramOutputStream());
    
    protected byte[] buffer;
    protected int length;
    protected int offset;
    protected int readWritePosition;
    
    DatagramImpl(byte[] buf, int len) {
        setData(buf, 0, len);
        _internalPacket= new DatagramPacket(buf, 0, len);
    }

    public String getAddress() {
        return _address;
    }

    public byte[] getData() {
        return buffer;
    }

    public int getLength() {
        return length;
    }

    public int getOffset() {
        return offset;
    }

    public void reset() {
        readWritePosition= 0;
        offset= 0;
        length= 0;
    }

    public void setAddress(Datagram reference) {
        setAddress(reference.getAddress());
    }

    public void setAddress(String addr) {
        PrimitiveURI uri= validateDatagramAddress(addr);
        if(uri.host == null) {
            throw new IllegalArgumentException("missing host");
        }
        
        setAddress(uri);
    }

    public void setData(byte[] buf, int off, int len) {
        /*
         * Check that the offset and length are valid. - must be positive - must
         * not exceed buffer length - must have valid buffer
         */
        if (len < 0 || off < 0 || (buf == null)
                || (off > 0 && off == buf.length) || ((len + off) > buf.length)
                || ((len + off) < 0)) {
            throw new IllegalArgumentException("Illegal length or offset");
        }

        buffer = buf;
        offset = off;
        length = len;
    }

    public void setLength(int len) {
        setData(buffer, offset, len);
    }

    public boolean readBoolean() throws IOException {
        return _reader.readBoolean();
    }

    public byte readByte() throws IOException {
        return _reader.readByte();
    }

    public char readChar() throws IOException {
        return _reader.readChar();
    }

    public double readDouble() throws IOException {
        return _reader.readDouble();
    }

    public float readFloat() throws IOException {
        return _reader.readFloat();
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        _reader.readFully(b, off, len);
    }

    public void readFully(byte[] b) throws IOException {
        _reader.readFully(b);
    }

    public int readInt() throws IOException {
        return _reader.readInt();
    }

    public String readLine() throws IOException {
        return _reader.readLine();
    }

    public long readLong() throws IOException {
        return _reader.readLong();
    }

    public short readShort() throws IOException {
        return _reader.readShort();
    }

    public int readUnsignedByte() throws IOException {
        return _reader.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
        return _reader.readUnsignedShort();
    }

    public String readUTF() throws IOException {
        return _reader.readUTF();
    }

    public int skipBytes(int n) throws IOException {
        return _reader.skipBytes(n);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        _writer.write(b, off, len);
    }

    public void write(byte[] b) throws IOException {
        _writer.write(b);
    }

    public void write(int b) throws IOException {
        _writer.write(b);
    }

    public void writeBoolean(boolean v) throws IOException {
        _writer.writeBoolean(v);
    }

    public void writeByte(int v) throws IOException {
        _writer.writeByte(v);
    }

    public void writeBytes(String s) throws IOException {
        _writer.writeBytes(s);
    }

    public void writeChar(int v) throws IOException {
        _writer.writeChar(v);
    }

    public void writeChars(String s) throws IOException {
        _writer.writeChars(s);
    }

    public void writeDouble(double v) throws IOException {
        _writer.writeDouble(v);
    }

    public void writeFloat(float v) throws IOException {
        _writer.writeFloat(v);
    }

    public void writeInt(int v) throws IOException {
        _writer.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        _writer.writeLong(v);
    }

    public void writeShort(int v) throws IOException {
        _writer.writeShort(v);
    }

    public void writeUTF(String str) throws IOException {
        _writer.writeUTF(str);
    }
    
    void setAddress(PrimitiveURI uri) {
        _socketAddress= new InetSocketAddress(uri.host, uri.port);
        _address= uri.rawString;
    }
    
    void doSend(DatagramSocket socket) throws IOException {
        _internalPacket.setSocketAddress(_socketAddress);
       _internalPacket.setData(buffer, offset, length);
       socket.send(_internalPacket);
    }
    
    void doReceive(DatagramSocket socket) throws IOException {
        int consumed= offset + length;
        _internalPacket.setData(buffer, consumed, buffer.length - consumed);
        socket.receive(_internalPacket);
        length= _internalPacket.getLength();
        _socketAddress= (InetSocketAddress) _internalPacket.getSocketAddress();
        _address= DATAGRAM_SCHEME + _internalPacket.getAddress().getHostName() + ":" + _internalPacket.getPort();
    }
}


