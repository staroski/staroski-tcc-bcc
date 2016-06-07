#!/bin/sh
# @version $Revision: 2550 $ ($Author: skarzhevskyy $) $Date: 2008-12-10 17:16:34 -0500 (Wed, 10 Dec 2008) $
#
SCRIPTS_DIR=`dirname "${0}"`/..
. "${SCRIPTS_DIR}/environment.sh"
if [[ ! "$?" = "0" ]]; then
    echo Error calling environment.sh
    exit 1
fi

export JAVAHOME=/opt/IBM/WEME/runtimes/61/lnx-x86-ppro10
export PATH=${JAVAHOME}/bin/:$PATH
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:${JAVAHOME}/bin

# igone j9 bugs *** glibc detected *** free(): invalid pointer:...
export MALLOC_CHECK_=0

JVM_ARGS="-jcl:ppro10"
JVM_ARGS="${JVM_ARGS} -Dmicroedition.connection.pkgs=com.intel.bluetooth"
#JVM_ARGS="${JVM_ARGS} -Dbluecove.debug=1"

${JAVAHOME}/bin/j9 ${JVM_ARGS} -cp "${BLUECOVE_TESTER_APP_JAR}" net.sf.bluecove.awt.Main
rc=$?
if [ ! "${rc}" = "0" ]; then
    echo Error calling java
    echo Prsess enter to exit
    read
fi
