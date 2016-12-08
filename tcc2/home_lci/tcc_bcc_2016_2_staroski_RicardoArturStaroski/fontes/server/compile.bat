set CATALINA_HOME=E:\tcc\runtime\apache-tomcat-8.0.38
set JAVA_HOME=E:\tcc\runtime\jdk1.8.0_112

set folder=..\client\obd-jrp



dir /B /S %folder%\*.java > %folder%\sources.txt

del /q %CATALINA_HOME%\webapps\obd-jrp-web\WEB-INF\classes\*
for /d %%x in (%CATALINA_HOME%\webapps\obd-jrp-web\WEB-INF\classes\*) do @rd /s /q "%%x"

copy %folder%\res\*.* %CATALINA_HOME%\webapps\obd-jrp-web\WEB-INF\classes

%JAVA_HOME%\bin\javac.exe -verbose -classpath %folder%\win-libs\bluecove-2.1.0.jar;%folder%\src -d %CATALINA_HOME%\webapps\obd-jrp-web\WEB-INF\classes @%folder%\sources.txt

set folder=obd-jrp-web

dir /B /S %folder%\*.java > %folder%\sources.txt
set cp=%CATALINA_HOME%\lib\servlet-api.jar;%CATALINA_HOME%\webapps\obd-jrp-web\WEB-INF\classes;%folder%\src;
%JAVA_HOME%\bin\javac.exe -verbose -classpath %cp% -d %CATALINA_HOME%\webapps\obd-jrp-web\WEB-INF\classes @%folder%\sources.txt