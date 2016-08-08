#!/bin/sh

# $Id: build-native-osx.sh 2487 2008-12-03 19:12:28Z skarzhevskyy $

BUILD_ROOT=`pwd`

cd src/main/c/intelbth

xcodebuild
BUILD_ERROR_CODE=$?

cd ${BUILD_ROOT}

cp src/main/resources/libbluecove.jnilib target/classes/

if [[ ${BUILD_ERROR_CODE} != 0  ]] ; then
  exit ${BUILD_ERROR_CODE}
fi
