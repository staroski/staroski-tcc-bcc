@echo off
rem @version $Revision: 2570 $ ($Author: skarzhevskyy $)  $Date: 2008-12-11 19:35:01 -0500 (Thu, 11 Dec 2008) $
SETLOCAL

call "%~dp0tck-environment.cmd" %*
if errorlevel 1 (
    echo Error calling tck-environment.cmd
    endlocal
    pause
    exit /b 1
)

SET STACK=emulator
title %STACK%-BluetoothTCK

set CP=%MICROEMULATOR_JAR%
set CP=%CP%;%BLUECOVE_JAR%
set CP=%CP%;%BLUECOVE_EMU_JAR%

rem >  run-%STACK%.cmd.log

java -Dbluecove.stack=%STACK% -cp "%CP%" %MICROEMULATOR_MAIN% %MICROEMULATOR_ARGS% -Xautotest:http://%BLUECOVE_TCK_HOST%:%BLUECOVE_TCK_PORT%/getNextApp.jad

if errorlevel 1 goto errormark
echo [Launched OK]
goto endmark
:errormark
	ENDLOCAL
	echo Error in run
:endmark
pause
ENDLOCAL
