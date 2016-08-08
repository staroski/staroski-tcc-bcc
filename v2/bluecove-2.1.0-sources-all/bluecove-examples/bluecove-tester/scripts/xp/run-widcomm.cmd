@echo off
rem @version $Revision: 2616 $ ($Author: skarzhevskyy $)  $Date: 2008-12-18 18:20:20 -0500 (Thu, 18 Dec 2008) $
SETLOCAL
call "%~dp0..\environment.cmd"
if errorlevel 1 goto endmark

SET STACK=widcomm
title %STACK%-tester

set JVM_ARGS=
set JVM_ARGS=%JVM_ARGS% -Dbluecove.connect.timeout=10000

set START_ARGS=-Dbluecove.native.path="%BLUECOVE_PROJECT_HOME%/src/main/resources" %BLUECOVE_MAIN%

java -cp "%BLUECOVE_TESTER_CP%" %JVM_ARGS% -Dbluecove.stack=%STACK% %START_ARGS% >  run-%STACK%.cmd.log
if errorlevel 2 (
    echo Error calling java
)
pause java ends.
:endmark
ENDLOCAL