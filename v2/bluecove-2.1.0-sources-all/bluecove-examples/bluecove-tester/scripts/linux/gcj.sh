#!/bin/sh
# @version $Revision: 2550 $ ($Author: skarzhevskyy $) $Date: 2008-12-10 17:16:34 -0500 (Wed, 10 Dec 2008) $
#
SCRIPTS_DIR=`dirname "${0}"`/..
. "${SCRIPTS_DIR}/environment.sh"
if [[ ! "$?" = "0" ]]; then
    echo Error calling environment.sh
    exit 1
fi

JAVA_HOME=/usr/lib/jvm/java-gcj

#echo BLUECOVE_TESTER_APP_JAR=${BLUECOVE_TESTER_APP_JAR}

${JAVA_HOME}/bin/java -classpath "${BLUECOVE_TESTER_APP_JAR}" ${BLUECOVE_MAIN} $*
