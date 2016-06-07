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

%WMDPT%\CECopy\cecopy "%DEFAULT_BUILD_HOME%\target\bluecove-tester-%VERSION%-%ASSEMBLY_ID%.jar" "dev:%BLUECOVE_INSTALL_DIR%\bluecove-tester.jar"

if errorlevel 1 goto errormark
echo [Copy OK]
goto endmark
:errormark
	ENDLOCAL
	echo Error in build
	pause
:endmark
ENDLOCAL
