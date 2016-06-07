#!/bin/sh
# @version $Revision: 2550 $ ($Author: skarzhevskyy $) $Date: 2008-12-10 17:16:34 -0500 (Wed, 10 Dec 2008) $
#
SCRIPTS_DIR=`dirname "${0}"`/..
. "${SCRIPTS_DIR}/environment.sh"
if [[ ! "$?" = "0" ]]; then
    echo Error calling environment.sh
    exit 1
fi

JVM_ARGS="-Dbluecove.deviceID=1"
#JVM_ARGS="${JVM_ARGS} -Dbluecove.debug=1"

java ${JVM_ARGS} -cp "${BLUECOVE_TESTER_APP_JAR}" ${BLUECOVE_MAIN} $*
rc=$?
if [ ! "${rc}" = "0" ]; then
    echo Error calling java
    echo Prsess enter to exit
    read
fi
