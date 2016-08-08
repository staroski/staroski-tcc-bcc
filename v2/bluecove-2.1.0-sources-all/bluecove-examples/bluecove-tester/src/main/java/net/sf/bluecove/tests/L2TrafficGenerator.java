/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2006-2008 Vlad Skarzhevskyy
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
 *  @version $Id: L2TrafficGenerator.java 2607 2008-12-17 23:51:33Z skarzhevskyy $
 */
package net.sf.bluecove.tests;

import java.io.IOException;

import org.bluecove.tester.log.Logger;
import org.bluecove.tester.util.IOUtils;
import org.bluecove.tester.util.RuntimeDetect;
import org.bluecove.tester.util.TimeUtils;

import net.sf.bluecove.CommunicationTesterL2CAP;
import net.sf.bluecove.Configuration;
import net.sf.bluecove.ConnectionHolderL2CAP;
import net.sf.bluecove.TestStatus;
import net.sf.bluecove.util.TimeStatistic;

/**
 *
 * 
 */
public class L2TrafficGenerator {

	static final int sequenceSizeMin = 16;

	private static class Config {

		int sequenceSleep = 0;

		int sequenceSize = 0;

		int durationMSec = 0;

		boolean init(byte[] initialData, boolean server, String messagePrefix) throws IOException {
			if (server) {
				if (initialData != null) {
					if (initialData.length >= 1) {
						sequenceSleep = IOUtils.byteToUnsignedInt(initialData[0]);
					}
					if (initialData.length >= 2) {
						sequenceSize = IOUtils.byteToUnsignedInt(initialData[1]);
					}
					if (initialData.length >= 3) {
						durationMSec = IOUtils.byteToUnsignedInt(initialData[2]);
					}
				}
			} else {
				sequenceSize = Configuration.tgSize & 0xFF;
				sequenceSleep = Configuration.tgSleep & 0xFF;
				durationMSec = Configuration.tgDurationMin;
			}

			sequenceSleep = sequenceSleep * 10;
			if (sequenceSize < sequenceSizeMin) {
				sequenceSize = sequenceSizeMin;
			}
			switch (sequenceSize) {
			case 251:
				// 1K
				sequenceSize = 0x400;
				break;
			case 252:
				// 2K
				sequenceSize = 0x800;
				break;
			case 253:
				// 3K
				sequenceSize = 0xC00;
				break;
			case 254:
				// 4K
				sequenceSize = 0x1000;
				break;
			case 255:
				// 5K
				sequenceSize = 0x1400;
				break;
			}
			Logger.debug(messagePrefix + " size selected " + sequenceSize + " byte");
			Logger.debug(messagePrefix + " duration " + durationMSec + " minutes");
			durationMSec *= 60000;
			return true;
		}
	}

	public static void trafficGeneratorClientInit(ConnectionHolderL2CAP c, int testType) throws IOException {
		byte sequenceSleep = (byte) (Configuration.tgSleep & 0xFF);
		byte sequenceSize = (byte) (Configuration.tgSize & 0xFF);
		byte durationMin = (byte) (Configuration.tgDurationMin & 0xFF);
		c.channel.send(CommunicationTesterL2CAP.startPrefix(testType, new byte[] { sequenceSleep, sequenceSize,
				durationMin }));
	}

	public static void trafficGeneratorWrite(ConnectionHolderL2CAP c, byte[] initialData, boolean server,
			final TestStatus testStatus) throws IOException {
		Config cf = new Config();
		if (!cf.init(initialData, server, "L2 write")) {
			return;
		}
		if (cf.sequenceSleep > 0) {
			Logger.debug("write sleep selected " + cf.sequenceSleep + " msec");
		} else {
			Logger.debug("write no sleep");
		}

		int transmitMTU = c.channel.getTransmitMTU();
		if (transmitMTU < cf.sequenceSize) {
			Logger.warn("L2 write size " + cf.sequenceSize + " is greater then MTU " + transmitMTU);
			cf.sequenceSize = transmitMTU;
		}

		long sequenceSentCount = 0;
		int reportedSize = 0;

		// Create test data
		byte[] data = new byte[cf.sequenceSize];
		for (int i = 1; i < cf.sequenceSize; i++) {
			data[i] = (byte) i;
		}
		long start = System.currentTimeMillis();
		long reported = start;
		try {
			mainLoop: do {
				IOUtils.long2Bytes(sequenceSentCount, 8, data, 0);
				long sendTime = System.currentTimeMillis();
				IOUtils.long2Bytes(sendTime, 8, data, 8);
				c.channel.send(data);
				sequenceSentCount++;
				reportedSize += cf.sequenceSize;
				c.active();
				long now = System.currentTimeMillis();
				if (now - reported > 5 * 1000) {
					Logger
							.debug("L2 Sent " + sequenceSentCount + " packet(s) "
									+ TimeUtils.bps(reportedSize, reported));
					reported = now;
					reportedSize = 0;
				}
				if ((cf.durationMSec != 0) && (now > start + cf.durationMSec)) {
					break;
				}
				if (cf.sequenceSleep > 0) {
					try {
						Thread.sleep(cf.sequenceSleep);
					} catch (InterruptedException e) {
						break mainLoop;
					}
					c.active();
				}
			} while (c.isConnectionOpen() && (!testStatus.isRunCompleate()));
		} finally {
			testStatus.setRunCompleate();
			Logger.debug("L2 Total " + sequenceSentCount + " packet(s)");
			Logger.debug("L2 Total write speed " + TimeUtils.bps(sequenceSentCount * cf.sequenceSize, start));
		}
	}

	public static void trafficGeneratorStatusReadStart(final ConnectionHolderL2CAP c, final TestStatus testStatus) {
		Runnable r = new Runnable() {
			public void run() {
				try {
					trafficGeneratorStatusRead(c, testStatus);
				} catch (IOException e) {
					Logger.error("reader", e);
				}
			}
		};
		Thread t = RuntimeDetect.cldcStub.createNamedThread(r, "L2tgStatusReciver");
		t.start();
	}

	public static void trafficGeneratorStatusRead(ConnectionHolderL2CAP c, final TestStatus testStatus)
			throws IOException {
		try {
			int receiveMTU = c.channel.getReceiveMTU();
			byte[] dataReceived = new byte[receiveMTU];
			mainLoop: do {
				if (!c.channel.ready()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						break mainLoop;
					}
				} else {
					c.channel.receive(dataReceived);
					c.active();
				}
			} while (c.isConnectionOpen() && (!testStatus.isRunCompleate()));
		} finally {
			testStatus.setRunCompleate();
		}
	}

	public static void trafficGeneratorReadStart(final ConnectionHolderL2CAP c, final byte[] initialData,
			final TestStatus testStatus) {
		Runnable r = new Runnable() {
			public void run() {
				try {
					trafficGeneratorRead(c, initialData, testStatus);
				} catch (IOException e) {
					Logger.error("reader", e);
				}
			}
		};
		Thread t = RuntimeDetect.cldcStub.createNamedThread(r, "L2tgReciver");
		t.start();
	}

	public static void trafficGeneratorRead(ConnectionHolderL2CAP c, byte[] initialData, final TestStatus testStatus)
			throws IOException {
		long sequenceRecivedCount = 0;
		long sequenceRecivedNumberLast = -1;
		long sequenceOutOfOrderCount = 0;
		TimeStatistic delay = new TimeStatistic();
		long start = System.currentTimeMillis();
		long reported = start;
		long receiveTimeLast = 0;
		long reportedSize = 0;
		long totalSize = 0;
		try {
			int receiveMTU = c.channel.getReceiveMTU();
			mainLoop: do {
				if (!c.channel.ready()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						break mainLoop;
					}
					continue;
				}
				byte[] dataReceived = new byte[receiveMTU];
				int lengthdataReceived = c.channel.receive(dataReceived);
				c.active();
				long receiveTime = System.currentTimeMillis();
				sequenceRecivedCount++;
				long sendTime = 0;
				reportedSize += lengthdataReceived;
				totalSize += lengthdataReceived;

				if (lengthdataReceived > 8) {
					long sequenceRecivedNumber = IOUtils.bytes2Long(dataReceived, 0, 8);
					if (sequenceRecivedNumberLast + 1 != sequenceRecivedNumber) {
						sequenceOutOfOrderCount++;
					} else if (lengthdataReceived > 18) {
						sendTime = IOUtils.bytes2Long(dataReceived, 8, 8);
					}
					sequenceRecivedNumberLast = sequenceRecivedNumber;
				} else {
					sequenceOutOfOrderCount++;
				}

				if (receiveTimeLast != 0) {
					delay.add(receiveTimeLast - receiveTime);
					receiveTimeLast = receiveTime;
				}

				long now = receiveTime;
				if (now - reported > 5 * 1000) {
					Logger.debug("L2 Received " + sequenceRecivedCount + "/" + sequenceOutOfOrderCount
							+ "(er) packet(s) " + delay.avg() + " msec");
					Logger.debug("L2 Received " + TimeUtils.bps(reportedSize, reported));
					reported = now;
					reportedSize = 0;
				}

			} while (c.isConnectionOpen() && (!testStatus.isRunCompleate()));
		} finally {
			testStatus.setRunCompleate();
			Logger.debug("L2 Total Received  " + sequenceRecivedCount + " packet(s)");
			Logger.debug("L2 Total Misplaced " + sequenceOutOfOrderCount + " packet(s)");
			Logger.debug("L2  avg interval " + delay.avg() + " msec");
			Logger.debug("L2 Total read speed " + TimeUtils.bps(totalSize, start));
		}
	}
}
