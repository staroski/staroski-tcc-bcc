@echo off
rem @version $Revision: 2570 $ ($Author: skarzhevskyy $)  $Date: 2008-12-11 19:35:01 -0500 (Thu, 11 Dec 2008) $

call "%~dp0..\environment.cmd"
if errorlevel 1 (
    echo Error calling environment.cmd
    exit /b 1
)

@if exist "%BLUECOVE_3RDPARTY_HOME%" goto 3p_found
@echo Error: 3-RD PARTY Folder [%BLUECOVE_3RDPARTY_HOME%] Not found
goto :errormark
:3p_found

set MICROEMULATOR_HOME=%BLUECOVE_3RDPARTY_HOME%\microemulator
set MICROEMULATOR_HOME=%D:\corp\me2\microemulator\microemulator\target

set MICROEMULATOR_JAR=%MICROEMULATOR_HOME%\microemulator.jar
set MICROEMULATOR_JAR=%MICROEMULATOR_HOME%\microemulator-2.0.3-SNAPSHOT.jar

set MICROEMULATOR_MAIN=org.microemu.app.Main
rem set MICROEMULATOR_MAIN=org.microemu.app.Headless
rem since 2.0.3
rem set MICROEMULATOR_ARGS=
rem set MICROEMULATOR_ARGS=--logCallLocation true --headless

@if exist "%MICROEMULATOR_JAR%" goto me2_found
@echo "Microemulator jar not found in directory %MICROEMULATOR_HOME%
goto :errormark
:me2_found

rem set BLUECOVE_TCK_HOST=localhost
set BLUECOVE_TCK_HOST=tckhost
set BLUECOVE_TCK_PORT=8080

set TCK_VERSION_ID=1-1-1_002
set GATLING_VERSION=1.0.05

set TCK_JSR82_HOME=%BLUECOVE_3RDPARTY_HOME%\TCK\JSR82_%TCK_VERSION_ID%_TCK

set GATLING_HOME=%BLUECOVE_3RDPARTY_HOME%\TCK\gatling-%GATLING_VERSION%

set GATLING_CORE_ROOT=%GATLING_HOME%\plugins\com.motorola.test.tckui_0.1.0\gatling_core

goto endmark
:errormark
    pause
	exit /b 1
:endmark

