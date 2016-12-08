set JAVA_HOME=E:\tcc\runtime\jdk1.8.0_112

set folder=obd-jrp
set cp=%folder%\win-libs\bluecove-2.1.0.jar;%folder%\bin;%folder%\res;
set app=br.com.staroski.obdjrp.ObdJrpScanData

%JAVA_HOME%\bin\java.exe -classpath %cp% %app%