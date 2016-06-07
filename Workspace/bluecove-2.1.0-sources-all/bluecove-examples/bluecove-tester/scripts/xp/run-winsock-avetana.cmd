@echo off
rem @version $Revision: 2549 $ ($Author: skarzhevskyy $)  $Date: 2008-12-10 15:38:46 -0500 (Wed, 10 Dec 2008) $
SETLOCAL
call "%~dp0..\environment.cmd"
if errorlevel 1 goto endmark

set JVM_ARGS=%JVM_ARGS% -Davetanabt.stack=microsoft

set CP=%BLUECOVE_3RDPARTY_HOME%\avetanaBluetooth\avetanaBluetooth.jar;%BLUECOVE_TESTER_JAR%;%MAVEN2_REPO%\junit\junit\3.8.1\junit-3.8.1.jar

java %JVM_ARGS% -cp "%CP%" %BLUECOVE_MAIN%
if errorlevel 1 (
    echo Error calling java
    pause
)
:endmark
ENDLOCAL