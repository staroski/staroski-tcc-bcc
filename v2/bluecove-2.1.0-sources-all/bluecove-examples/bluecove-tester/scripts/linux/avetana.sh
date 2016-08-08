#!/bin/sh
# @version $Revision: 2616 $ ($Author: skarzhevskyy $) $Date: 2008-12-18 18:20:20 -0500 (Thu, 18 Dec 2008) $
#
SCRIPTS_DIR=`dirname ${0}`/..
. ${SCRIPTS_DIR}/environment.sh

java -cp ${BLUECOVE_3RDPARTY_HOME}/avetanaBluetooth/avetanaBluetooth.jar:${BLUECOVE_TESTER_JAR} ${BLUECOVE_MAIN} $*
rc=$?
if [ ! "${rc}" = "0" ]; then
    echo Error calling java
    echo Prsess enter to exit
    read
fi

