/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2007-2008 Vlad Skarzhevskyy
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
 *  @version $Id: JSR82APIObexDeclarationsTest.java 2471 2008-12-01 03:44:20Z skarzhevskyy $
 */
package javax.obex;

import net.sf.jour.signature.SignatureTestCase;

/**
 *
 */
public class JSR82APIObexDeclarationsTest extends SignatureTestCase {

    /* (non-Javadoc)
     * @see net.sf.jour.signature.SignatureTestCase#getAPIPath()
     */
    public String getAPIPath() {
        return getClassPath(Authenticator.class);
    }

    /* (non-Javadoc)
     * @see net.sf.jour.signature.SignatureTestCase#getSignatureXMLPath()
     */
    public String getSignatureXMLPath() {
        return "jsr82-obex-signature.xml";
    }

}
