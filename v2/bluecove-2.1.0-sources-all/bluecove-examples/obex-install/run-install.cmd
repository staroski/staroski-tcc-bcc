@echo off
rem @version $Revision: 2549 $ ($Author: skarzhevskyy $)  $Date: 2008-12-10 15:38:46 -0500 (Wed, 10 Dec 2008) $
SETLOCAL
call "%~dp0..\bluecove-tester\scripts\version.cmd"

rem set JVM_ARGS=%JVM_ARGS% -Dbluecove.debug=1

java %JVM_ARGS% -cp target\obex-install-%BLUECOVE_VERSION%-mini.jar net.sf.bluecove.obex.Deploy btgoep://0019639c4007:6  src\main\resources\icon.png
if errorlevel 1 (
    echo Error calling java
    pause
)

ENDLOCAL