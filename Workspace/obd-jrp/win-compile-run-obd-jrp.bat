cls

echo "Compiling OBD-JRP..."
%JAVA_HOME%\bin\javac.exe -encoding UTF-8 -classpath ".\arm-libs\bluecove-gpl-2.1.0-SNAPSHOT.jar;.\arm-libs\bluecove-2.1.0-SNAPSHOT.jar;.\arm-libs" -sourcepath .\src -d .\bin .\src\*.java
echo "Compilation finished!"

echo "Executing OBD-JRP..."
%JAVA_HOME%\bin\java.exe -classpath ".\arm-libs\bluecove-gpl-2.1.0-SNAPSHOT.jar;.\arm-libs\bluecove-2.1.0-SNAPSHOT.jar;.\arm-libs;.\bin" ObdJrp
echo "Execution finished!"
