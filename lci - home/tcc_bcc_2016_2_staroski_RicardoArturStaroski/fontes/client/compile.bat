set JAVA_HOME=E:\tcc\runtime\jdk1.8.0_112

set folder=obd-jrp

dir /B /S %folder%\*.java > %folder%\sources.txt

del /q %folder%\bin\*
for /d %%x in (%folder%\bin\*) do @rd /s /q "%%x"

%JAVA_HOME%\bin\javac.exe -verbose -classpath %folder%\win-libs\bluecove-2.1.0.jar;%folder%\src -d %folder%\bin @%folder%\sources.txt
