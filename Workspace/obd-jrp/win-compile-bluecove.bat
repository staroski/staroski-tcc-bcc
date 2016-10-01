echo "Compiling bluecove..."
cd ..\bluecove-2.1.1-sources\bluecove-2.1.1
ant all
copy /Y .\target\*.jar ..\..\obd-jrp\win-libs
cd ..\..\obd-jrp\
echo "Compilation finished!"