javac -classpath ".\arm-runtime-libs;.\arm-runtime-libs\bluecove-2.1.1-SNAPSHOT.jar;.\bluecove-gpl-2.1.1-SNAPSHOT.jar" -sourcepath .\src -d .\bin .\src\*.java

java -classpath ".\bin;.\arm-runtime-libs;.\arm-runtime-libs\bluecove-2.1.1-SNAPSHOT.jar;.\bluecove-gpl-2.1.1-SNAPSHOT.jar" TesteJanela


