#!/bin/sh
# @version $Revision: 2570 $ ($Author: skarzhevskyy $) $Date: 2008-12-11 19:35:01 -0500 (Thu, 11 Dec 2008) $
#
SCRIPTS_DIR=`dirname "${0}"`/..
. "${SCRIPTS_DIR}/environment.sh"
if [[ ! "$?" = "0" ]]; then
    echo Error calling environment.sh
    exit 1
fi

BLUECOVE_TCK_HOST=tckhost
BLUECOVE_TCK_PORT=8080

if [[ ! -d ${BLUECOVE_3RDPARTY_HOME} ]] ; then
  echo "Invalid 3-rd party directory ${BLUECOVE_3RDPARTY_HOME}"
  exit 1
fi

MICROEMULATOR_HOME=${BLUECOVE_3RDPARTY_HOME}/microemulator

MICROEMULATOR_MAIN=org.microemu.app.Main
MICROEMULATOR_ARGS=
#since 2.0.3
#MICROEMULATOR_MAIN=org.microemu.app.Headless
#MICROEMULATOR_ARGS="--logCallLocation true --headless"
