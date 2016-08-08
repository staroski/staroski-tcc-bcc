@echo off
rem @version $Revision: 2549 $ ($Author: skarzhevskyy $)  $Date: 2008-12-10 15:38:46 -0500 (Wed, 10 Dec 2008) $
SETLOCAL
call "%~dp0..\environment.cmd"
if errorlevel 1 goto endmark

set JVM_ARGS=%JVM_ARGS% -Dbluecove.debug=1
set JVM_ARGS=%JVM_ARGS% -Dbluecove.stack.first=widcomm

java %JVM_ARGS% -jar "%BLUECOVE_TESTER_APP_JAR%"
if errorlevel 1 (
    echo Error calling java
    pause
)
:endmark
ENDLOCAL