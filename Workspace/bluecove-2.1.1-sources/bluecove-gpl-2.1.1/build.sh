#!/bin/sh
# @version $Revision: 2808 $ ($Author: skarzhevskyy $) $Date: 2009-02-26 19:08:41 -0500 (Thu, 26 Feb 2009) $
#
# Created by Francois Kooman
#
# Use this file in case you don't have ant or maven installed on the system
#
# requirements: gcc, javac, javah, bluez

BUILD_DIR=`pwd`

BLUECOVE_VERSION=2.1.1-SNAPSHOT

SRC_JAVA_DIR=${BUILD_DIR}/src/main/java
SRC_C_DIR=${BUILD_DIR}/src/main/c
CLASSES_DIR=${BUILD_DIR}/target/classes
OBJ_DIR=${BUILD_DIR}/target/native
JAVAC_OPTIONS="-g -Xlint:unchecked -source 1.3 -target 1.1"

BLUECOVE_JAR="${BUILD_DIR}/../bluecove/target/bluecove-${BLUECOVE_VERSION}.jar"
if [[ ! -f ${BLUECOVE_JAR} ]] ; then
  echo "BlueCove jar not found ${BLUECOVE_JAR}"
  exit 1
fi

mkdir -p ${CLASSES_DIR}

echo "=== Compile the bluez stack java files ==="
javac -d ${CLASSES_DIR} ${JAVAC_OPTIONS} -cp ${BLUECOVE_JAR} ${SRC_JAVA_DIR}/com/intel/bluetooth/BluetoothStackBlueZ*.java
if [[ ! "$?" = "0" ]]; then
    echo Error in Java compilation
    exit 1
fi

echo "=== Generate the JNI C header files from these java files ==="
javah -d ${SRC_C_DIR} -classpath ${CLASSES_DIR} com.intel.bluetooth.BluetoothStackBlueZ com.intel.bluetooth.BluetoothStackBlueZConsts com.intel.bluetooth.BluetoothStackBlueZNativeTests
if [[ ! "$?" = "0" ]]; then
    echo Error in JNI haders creation
    exit 1
fi

echo "=== Compile the C files ==="
mkdir -p ${OBJ_DIR}
cd ${OBJ_DIR}
gcc -fPIC -c ${SRC_C_DIR}/*.c -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux
if [[ ! "$?" = "0" ]]; then
    echo Error in C compilation
    cd ${BUILD_DIR}
    exit 1
fi
cd ${BUILD_DIR}

echo "=== Link object file into the library ==="
gcc -shared -lbluetooth -Wl,-soname,libbluecove-${BLUECOVE_VERSION} -o ${BUILD_DIR}/target/libbluecove.so ${OBJ_DIR}/*.o
if [[ ! "$?" = "0" ]]; then
    echo Error in linking
    exit 1
fi

# copy the shared library to classes directory
cp ${BUILD_DIR}/target/libbluecove.so ${CLASSES_DIR}

echo "Native library ${BUILD_DIR}/target/libbluecove.so created"


