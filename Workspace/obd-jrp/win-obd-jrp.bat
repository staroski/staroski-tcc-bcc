cls

rem ambiente senior
if exist C:\Program Files (x86)\Java\jdk1.8.0_101 (
  echo "Updating JAVA_HOME..."
  set JAVA_HOME=C:\Program Files (x86)\Java\jdk1.8.0_101
  echo "JAVA_HOME  updated to %JAVA_HOME%"
)

echo "Executing OBD-JRP..."
"%JAVA_HOME%\bin\java.exe" -classpath ".\win-libs\bluecove-2.1.1-SNAPSHOT.jar;.\bin" ObdJrp
echo "Execution finished!"
