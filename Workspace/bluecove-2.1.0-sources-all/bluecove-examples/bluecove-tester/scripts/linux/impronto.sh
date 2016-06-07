#! /bin/sh
#  @version $Revision: 2152 $ ($Author: skarzhevskyy $) $Date: 2008-05-01 17:52:41 -0400 (Thu, 01 May 2008) $
#
unset JVM_ARGS
LD_LIBRARY_PATH=/usr/lib/:${LD_LIBRARY_PATH}
export LD_LIBRARY_PATH
# JVM_ARGS=%JVM_ARGS%

java -cp /usr/share/java/idev_bluez.jar:junit-3.8.1.jar:bluecove-tester-2.0.1-SNAPSHOT.jar net.sf.bluecove.awt.Main
