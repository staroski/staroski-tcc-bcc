@echo off
rem @version $Revision: 2549 $ ($Author: skarzhevskyy $)  $Date: 2008-12-10 15:38:46 -0500 (Wed, 10 Dec 2008) $
SETLOCAL

call "%~dp0tck-environment.cmd" %*
if errorlevel 1 (
    echo Error calling tck-environment.cmd
    endlocal
    pause
    exit /b 1
)

set BLUECOVE_JAR=%BLUECOVE_3RDPARTY_HOME%\avetanaBluetooth\avetanaBluetooth.jar
SET STACK=avetana-widcomm
title %STACK%-BluetoothTCK

set JVM_ARGS=
set JVM_ARGS=%JVM_ARGS% -Dde.avetana.bluetooth.debug=true

set CP=%MICROEMULATOR_HOME%\microemulator.jar;%BLUECOVE_JAR%

java %JVM_ARGS% -cp "%CP%" org.microemu.app.Main -Xautotest:http://%BLUECOVE_TCK_HOST%:%BLUECOVE_TCK_PORT%/getNextApp.jad >  run-%STACK%.cmd.log

if errorlevel 1 goto errormark
echo [Launched OK]
goto endmark
:errormark
	ENDLOCAL
	echo Error in start
	pause
:endmark
ENDLOCAL
