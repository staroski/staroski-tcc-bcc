/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
 * 
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 *  @author vlads
 *  @version $Id: IOUtils.java 2611 2008-12-18 06:47:42Z skarzhevskyy $
 */
package org.bluecove.tester.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.microedition.io.Connection;

import org.bluecove.tester.log.Logger;

/**
 * General IO stream manipulation utilities. closeQuietly functions are based on
 * org.apache.commons.io and used here for J2ME
 * 
 * <p>
 * This class provides static utility methods for input/output operations.
 * <ul>
 * <li>closeQuietly - these methods close a stream ignoring nulls and all
 * exceptions
 * </ul>
 * <p>
 */

public class IOUtils {

    /**
     * Close an <code>InputStream</code>.
     * <p>
     * Calls {@link InputStream#close()}, any exceptions will be ignored. This
     * is used in finally blocks.
     * 
     * @param input
     *            the InputStream to close, may be null or already closed
     */
    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (Throwable e) {
            if (RuntimeDetect.isBlueCove) {
                Logger.error("InputStream.close()", e);
            }
        }
    }

    /**
     * Close an <code>OutputStream</code>.
     * <p>
     * Calls {@link OutputStream#close()}, any exceptions will be ignored. This
     * is used in finally blocks.
     * 
     * @param output
     *            the OutputStream to close, may be null or already closed
     */
    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (Throwable e) {
            if (RuntimeDetect.isBlueCove) {
                Logger.error("OutputStream.close()", e);
            }
        }
    }

    /**
     * Close a <code>Writer</code>.
     * <p>
     * Calls {@link Writer#close()}, any exceptions will be ignored. This is
     * used in finally blocks.
     * 
     * @param output
     *            the Writer to close, may be null or already closed
     */
    public static void closeQuietly(Writer output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (Throwable ioe) {
            // ignore
        }
    }

    public static void closeQuietly(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (Throwable e) {
            if (RuntimeDetect.isBlueCove) {
                Logger.error("Connection.close()", e);
            }
        }
    }

    public static byte hiByte(int value) {
        return (byte) ((value >> 8) & 0xFF);
    }

    public static byte loByte(int value) {
        return (byte) (0xFF & value);
    }

    public static int bytesToShort(byte valueHi, byte valueLo) {
        return ((((int) valueHi << 8) & 0xFF00) + (valueLo & 0xFF));
    }

    public static void long2Bytes(long value, int size, byte[] b, int offset) {
        for (int i = 0; i < size; i++) {
            b[offset + i] = (byte) ((value >> (size - 1 << 3) & 0xFF));
            value <<= 8;
        }
    }

    public static int byteToUnsignedInt(byte value) {
        int i = value;
        if (i < 0) {
            i = 0x100 + i;
        }
        return i;
    }

    public static long bytes2Long(byte[] b, int offset, int size) {
        long value = 0;
        for (int i = 0; i < size; i++) {
            value = (value << 8) | (b[offset + i] & 0xFF);
        }
        return value;
    }

    public static byte[] writeByteArray(IsExternalizable streamable) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(baos);
            streamable.writeExternal(outputStream);
            outputStream.flush();
            // Extract the byte array
            return baos.toByteArray();
        } finally {
            closeQuietly(baos);
        }
    }

}