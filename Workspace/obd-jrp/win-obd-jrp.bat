cls

echo "Executing OBD-JRP..."
%JAVA_HOME%\bin\java.exe -classpath ".\arm-libs\bluecove-gpl-2.1.0-SNAPSHOT.jar;.\arm-libs\bluecove-2.1.0-SNAPSHOT.jar;.\arm-libs;.\bin" ObdJrp
echo "Execution finished!"
