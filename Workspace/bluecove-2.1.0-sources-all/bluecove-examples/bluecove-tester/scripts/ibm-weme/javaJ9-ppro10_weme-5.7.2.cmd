@echo off
rem @version $Revision: 2549 $ ($Author: skarzhevskyy $)  $Date: 2008-12-10 15:38:46 -0500 (Wed, 10 Dec 2008) $
SETLOCAL
call "%~dp0..\environment.cmd"
if errorlevel 1 goto endmark


@rem set JAVA_HOME=%ProgramFiles%\IBM\DeviceDeveloper\wsdd5.0\ive-2.2\runtimes\win32\x86\ppro10
@set JAVA_HOME=%ProgramFiles%\IBM\DeviceDeveloper\wsdd5.0\ive-2.2

@if exist "%JAVA_HOME%\bin" goto 3p_found
@echo Error: JAVA not found in folder [%JAVA_HOME%]
pause
goto :endmark
:3p_found


set JVM_ARGS=
set JVM_ARGS=%JVM_ARGS% -jcl:ppro10

set JVM_ARGS=%JVM_ARGS% -Dmicroedition.connection.pkgs=com.intel.bluetooth
rem set JVM_ARGS=%JVM_ARGS% -Dbluecove.debug=1

call "%~dp0..\set-stack.cmd"

title J9 PPRO1.0-v5.7.2

%JAVA_HOME%\bin\j9 %JVM_ARGS% -classpath "%BLUECOVE_TESTER_APP_JAR%" %BLUECOVE_MAIN%

pause
:endmark
ENDLOCAL