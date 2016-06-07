@echo off
rem @version $Revision: 103 $ ($Author: skarzhevskyy $)  $Date: 2007-02-25 12:25:48 -0500 (Sun, 25 Feb 2007) $
title *Jetty:microemu-webstart

call mvn -o -P debug webstart:jnlp
echo Go to http://localhost:8080/bluecove-webstart/
call mvn %* jetty:run

title Jetty:microemu-webstart - ended

pause
