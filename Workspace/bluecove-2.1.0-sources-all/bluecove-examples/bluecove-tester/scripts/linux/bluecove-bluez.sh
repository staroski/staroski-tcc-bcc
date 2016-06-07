#!/bin/sh
# @version $Revision: 2616 $ ($Author: skarzhevskyy $) $Date: 2008-12-18 18:20:20 -0500 (Thu, 18 Dec 2008) $
#
SCRIPTS_DIR=`dirname "${0}"`/..
. "${SCRIPTS_DIR}/environment.sh"
if [[ ! "$?" = "0" ]]; then
    echo Error calling environment.sh
    exit 1
fi

BLUECOVE_BLUEZ_CP="${BLUECOVE_JAR}:${BLUECOVE_BLUEZ_JAR}"
BLUECOVE_BLUEZ_CP="${BLUECOVE_BLUEZ_CP}:${BLUECOVE_BLUEZ_PROJECT_HOME}/target/dbus.jar"
BLUECOVE_BLUEZ_CP="${BLUECOVE_BLUEZ_CP}:${BLUECOVE_BLUEZ_PROJECT_HOME}/target/debug-disable.jar"
BLUECOVE_BLUEZ_CP="${BLUECOVE_BLUEZ_CP}:${BLUECOVE_BLUEZ_PROJECT_HOME}/target/unixsockets.jar"

BLUECOVE_BLUEZ_CP="${BLUECOVE_BLUEZ_CP}:${BLUECOVE_TESTER_JAR}"

java -cp "${BLUECOVE_BLUEZ_CP}" ${BLUECOVE_MAIN} $*
rc=$?
if [ ! "${rc}" = "0" ]; then
    echo Error calling java
    echo Prsess enter to exit
    read
fi
