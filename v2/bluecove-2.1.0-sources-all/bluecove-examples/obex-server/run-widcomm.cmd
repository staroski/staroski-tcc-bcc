@echo off
rem @version $Revision: 2549 $ ($Author: skarzhevskyy $)  $Date: 2008-12-10 15:38:46 -0500 (Wed, 10 Dec 2008) $
SETLOCAL
call "%~dp0..\bluecove-tester\scripts\version.cmd"
SET STACK=widcomm
title %STACK%-obex-server
rem set JVM_ARGS=%JVM_ARGS% -Dbluecove.debug=1
java -Dbluecove.stack=%STACK% %JVM_ARGS% -jar target\obex-server-%BLUECOVE_VERSION%-app.jar
if errorlevel 2 (
    echo Error calling java
)
pause java ends.
ENDLOCAL