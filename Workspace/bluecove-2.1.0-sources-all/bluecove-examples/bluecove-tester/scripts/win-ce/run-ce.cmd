@echo off
rem @version $Revision: 2549 $ ($Author: skarzhevskyy $)  $Date: 2008-12-10 15:38:46 -0500 (Wed, 10 Dec 2008) $
SETLOCAL

call "%~dp0scripts\environment.cmd" %*
if errorlevel 1 (
    echo Error calling environment.cmd
    endlocal
    pause
    exit /b 1
)

rem cd ..\..\bluecove
call m2
rem cd %~dp0
rem call m2
call scripts\install-ce.cmd

if NOT '%WIN_CE_PHONE%' EQU 'true' (
    if '%JVM_MYSAIFU%' EQU 'true' (
        call scripts\run-ce-Mysaifu.cmd
    ) else (
        call scripts\run-ce-IBM.cmd
    )
)

if '%WIN_CE_PHONE%' EQU 'true' (
    call scripts\run-ce-Mysaifu-phone.cmd
)
ENDLOCAL