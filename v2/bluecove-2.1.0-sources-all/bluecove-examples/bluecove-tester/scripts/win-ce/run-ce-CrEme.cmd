@echo off
rem @version $Revision: 2549 $ ($Author: skarzhevskyy $)  $Date: 2008-12-10 15:38:46 -0500 (Wed, 10 Dec 2008) $
SETLOCAL

call "%~dp0environment.cmd" %*
if errorlevel 1 (
    echo Error calling environment.cmd
    endlocal
    pause
    exit /b 1
)

%WMDPT%\RAPI_Start\rapistart \Windows\CrEme\bin\CrEme.exe -jar \bluecove\bluecove-tester.jar

if errorlevel 1 goto errormark
echo [Launched OK]
goto endmark
:errormark
	ENDLOCAL
	echo Error in build
	pause
:endmark
ENDLOCAL
