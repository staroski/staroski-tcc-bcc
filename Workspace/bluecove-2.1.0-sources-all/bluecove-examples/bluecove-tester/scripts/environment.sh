#!/bin/sh
# @version $Revision: 2550 $ ($Author: skarzhevskyy $) $Date: 2008-12-10 17:16:34 -0500 (Wed, 10 Dec 2008) $
#
# echo SCRIPTS_DIR=${SCRIPTS_DIR}
#make absolute path
#pushd ${SCRIPTS_DIR}
SAVE_DIR=`pwd`
cd "${SCRIPTS_DIR}"
SCRIPTS_DIR=`pwd`
#popd
cd "${SAVE_DIR}"
# echo SCRIPTS_DIR=${SCRIPTS_DIR}
. "${SCRIPTS_DIR}/version.sh"
if [[ ! "$?" = "0" ]]; then
    echo Error calling version.sh
    exit 1
fi

BLUECOVE_TESTER_HOME=`dirname "${SCRIPTS_DIR}"`
BLUECOVE_HOME=`dirname "${BLUECOVE_TESTER_HOME}"`
BLUECOVE_HOME=`dirname "${BLUECOVE_HOME}"`
BLUECOVE_PROJECT_HOME=${BLUECOVE_HOME}/bluecove
BLUECOVE_JAR="${BLUECOVE_PROJECT_HOME}/target/bluecove-${BLUECOVE_VERSION}.jar"
BLUECOVE_GPL_PROJECT_HOME=${BLUECOVE_HOME}/bluecove-gpl
BLUECOVE_GPL_JAR="${BLUECOVE_GPL_PROJECT_HOME}/target/bluecove-gpl-${BLUECOVE_VERSION}.jar"

BLUECOVE_BLUEZ_PROJECT_HOME=${BLUECOVE_HOME}/bluecove-bluez
BLUECOVE_BLUEZ_JAR="${BLUECOVE_BLUEZ_PROJECT_HOME}/target/bluecove-bluez-${BLUECOVE_VERSION}.jar"

BLUECOVE_TESTER_JAR="${BLUECOVE_TESTER_HOME}/target/bluecove-tester-${BLUECOVE_VERSION}.jar"
BLUECOVE_TESTER_APP_JAR="${BLUECOVE_TESTER_HOME}/target/bluecove-tester-${BLUECOVE_VERSION}-app.jar"
BLUECOVE_3RDPARTY_HOME=`dirname "${BLUECOVE_HOME}"`/3p
BLUECOVE_MAIN=net.sf.bluecove.se.Main
#echo BLUECOVE_TESTER_APP_JAR=${BLUECOVE_TESTER_APP_JAR}

if [[ ! -f ${BLUECOVE_JAR} ]] ; then
  echo "BlueCove not found ${BLUECOVE_JAR}"
  exit 1
fi