set JAVA_HOME=E:\tcc\runtime\jdk1.8.0_112

set folder=obd-jrp
set cp=%folder%\bin;%folder%\res;
set app=br.com.staroski.obdjrp.ObdJrpUploadData

%JAVA_HOME%\bin\java.exe -classpath %cp% %app%