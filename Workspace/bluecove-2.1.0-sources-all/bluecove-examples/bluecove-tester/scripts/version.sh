#!/bin/sh
# @version $Revision: 2550 $ ($Author: skarzhevskyy $) $Date: 2008-12-10 17:16:34 -0500 (Wed, 10 Dec 2008) $
#
GENERATED_VERSION="${SCRIPTS_DIR}/generated-version.sh"
if [ ! -f "${GENERATED_VERSION}" ]; then
    echo "${GENERATED_VERSION} Not Found, run maven first"
    exit 1;
fi
chmod +x "${GENERATED_VERSION}"
. "${GENERATED_VERSION}"
# echo BLUECOVE_VERSION=${BLUECOVE_VERSION}
