#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ(2.57)
AC_INIT(FULL-PACKAGE-NAME, VERSION, BUG-REPORT-ADDRESS)
AC_CONFIG_SRCDIR([c/BlueZ.cpp])
AC_CONFIG_HEADER([config.h])

# Checks for programs.
AC_PROG_CC
AC_PROG_MAKE_SET

# Checks for libraries.
AC_DEFUN(CHECK_BLUEZ,
[ AC_CHECK_HEADERS(bluetooth/bluetooth.h)
  LIB_BLUETOOTH=
  AC_CHECK_LIB(bluetooth,hci_open_dev,
	[LIB_BLUETOOTH="-lbluetooth"],
	[AC_MSG_ERROR([Sorry, you should install the libbluetooth library before trying to compile this package])],
	[] )

  AC_SUBST(LIB_BLUETOOTH)
])

CHECK_BLUEZ

#setting default values for JDK_HOME, PLATFORM, and BIN_DIR
#JDK_HOME=($JDK_HOME)
#JDK_PLATFORM="linux"
#BIN_DIR="/usr/local/bin"

#check for java home directory

JDK_HOME=
 AC_ARG_WITH(jdk, [--with-jdk=PATH	path for value of JDK_HOME], [
	if test -n "$withval"; then
		JDK_HOME="$withval"
	fi
 ])

AC_SUBST(JDK_HOME)


JDK_PLATFORM=
AC_ARG_WITH(javaplatform, [--with-javaplatform=PLATFORM 	name of current java platform ], [
	if test -n "$withval"; then
		JDK_PLATFORM="$withval"
	fi
])

AC_SUBST(JDK_PLATFORM)

#set the BIN_DIR directory (the directory where the rssi_enabled_jbluez.jar will be located)
BIN_DIR=
AC_ARG_WITH(bindir, [--with-bindir=BIN_DIR 	destination PATH for the produced binaries], [
	if test -n "$withval"; then
		BIN_DIR="$withval"
	fi
])
AC_SUBST(BIN_DIR)


# Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADERS([stdlib.h sys/ioctl.h sys/socket.h unistd.h])

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.
AC_PROG_GCC_TRADITIONAL
AC_FUNC_MALLOC
AC_CHECK_FUNCS([memset])

AC_CONFIG_FILES([Makefile
                 c/Makefile
         	])
AC_OUTPUT
