@echo off
rem @version $Revision: 2549 $ ($Author: skarzhevskyy $)  $Date: 2008-12-10 15:38:46 -0500 (Wed, 10 Dec 2008) $
SETLOCAL
call "%~dp0..\environment.cmd"
if errorlevel 1 goto endmark

SET STACK=widcomm
title bluecove-2.0.1-tester
java  -Dbluecove.stack=%STACK% -classpath "%BLUECOVE_TESTER_HOME%\src\site\resources\bluecove-2.0.1-signed.jar;%BLUECOVE_TESTER_APP_JAR%" %BLUECOVE_MAIN% >  run-%STACK%-2.0.1.cmd.log
if errorlevel 1 (
    echo Error calling java
    pause
)
:endmark
ENDLOCAL