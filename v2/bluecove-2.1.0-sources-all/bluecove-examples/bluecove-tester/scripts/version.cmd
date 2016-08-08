@echo off
rem @version $Revision: 2549 $ ($Author: skarzhevskyy $)  $Date: 2008-12-10 15:38:46 -0500 (Wed, 10 Dec 2008) $

if exist "%~dp0generated-version.cmd" goto generated_version_found
echo 
echo %~dp0generated-version.cmd Not Found, run maven first
goto :errormark

:generated_version_found
call "%~dp0generated-version.cmd"
rem echo BLUECOVE_VERSION=%BLUECOVE_VERSION%
goto :endmark

:errormark
	exit /b 1
:endmark