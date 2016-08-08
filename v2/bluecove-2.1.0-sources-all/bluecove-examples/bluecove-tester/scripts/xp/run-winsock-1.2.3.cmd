@echo off
rem @version $Revision: 2549 $ ($Author: skarzhevskyy $)  $Date: 2008-12-10 15:38:46 -0500 (Wed, 10 Dec 2008) $
SETLOCAL
call "%~dp0..\environment.cmd"
if errorlevel 1 goto endmark

title bluecove-1.2.3-tester

set CP=%BLUECOVE_TESTER_HOME%src\site\resources\bluecove-1.2.3-signed.jar;%BLUECOVE_TESTER_APP_JAR%

java -classpath "%CP%" %BLUECOVE_MAIN% >  run-winsock-1.2.3.cmd.log
if errorlevel 1 (
    echo Error calling java
    pause
)
:endmark
ENDLOCAL