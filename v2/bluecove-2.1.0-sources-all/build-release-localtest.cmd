@echo off
rem @version $Revision: 2673 $ ($Author: skarzhevskyy $)  $Date: 2008-12-25 17:38:21 -0500 (Thu, 25 Dec 2008) $
rem helper to run maven2
rem
if not defined MAVEN_OPTS  set MAVEN_OPTS=-Xmx256M

rem call mvn clean
rem @if errorlevel 1 goto errormark

call mvn clean deploy -P build,release -DperformRelease=true
@if errorlevel 1 goto errormark

call mvn site-deploy -P build,release -DperformRelease=true
@if errorlevel 1 goto errormark
@goto endmark
:errormark
	@echo Error in build
    pause
:endmark
