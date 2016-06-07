@echo off
rem @version $Revision: 285 $ ($Author: skarzhevskyy $)  $Date: 2007-05-20 01:44:55 -0400 (Sun, 20 May 2007) $
title *Jetty:bluecove-tester

call mvn -o package site
echo Go to http://localhost:8080/bluecove-tester/awt-bctest-localhost.jnlp
call mvn %* jetty:run

title Jetty:bluecove-tester - ended

pause
