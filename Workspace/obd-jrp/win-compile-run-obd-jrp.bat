cls

rem ambiente senior
if exist C:\Program Files (x86)\Java\jdk1.8.0_101 (
  echo "Updating JAVA_HOME..."
  set JAVA_HOME=C:\Program Files (x86)\Java\jdk1.8.0_101
  echo "JAVA_HOME  updated to %JAVA_HOME%"
)

cmd /C win-compile-bluecove.bat

echo "Compiling OBD-JRP..."
"%JAVA_HOME%\bin\javac.exe" -encoding UTF-8 -classpath ".\win-libs\bluecove-2.1.1-SNAPSHOT.jar;" -sourcepath .\src -d .\bin .\src\*.java
echo "Compilation finished!"

echo "Executing OBD-JRP..."
"%JAVA_HOME%\bin\java.exe" -classpath ".\win-libs\bluecove-2.1.1-SNAPSHOT.jar;.\bin" ObdJrp
echo "Execution finished!"
