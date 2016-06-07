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

SET STACK=widcomm
title %STACK%-BluetoothTCKAgent
java -Dbluecove.stack=%STACK% -cp "%TCK_JSR82_HOME%\Bluetooth_%TCK_VERSION_ID%_TCK\BluetoothTCKAgent.zip;%BLUECOVE_JAR%" BluetoothTCKAgent.BluetoothTCKAgentApp

if errorlevel 1 goto errormark
echo [Launched OK]
goto endmark
:errormark
	ENDLOCAL
	echo Error in start
	pause
:endmark
ENDLOCAL
