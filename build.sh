#!/bin/sh

# История одного buildnumber
echo -n $(($(cat buildnumber | cut -d ',' -f 1)+1)), $(date +'%d.%m.%Y') > buildnumber.txt
mv buildnumber.txt buildnumber

# Build Launcher.jar
echo Building Launcher.jar...
"C:\Users\mrcat\.jdks\liberica-1.8.0_282\bin\jar.exe" -uf Launcher.jar buildnumber
"C:\Users\mrcat\.jdks\liberica-1.8.0_282\bin\java.exe" -jar build/proguard.jar @Launcher.pro
rm Launcher.jar
mv Launcher-obf.jar Launcher.jar
# java -jar build/stringer.jar -configFile Launcher.stringer Launcher.jar Launcher.jar
"C:\Users\mrcat\.jdks\liberica-1.8.0_282\bin\pack200.exe" -E9 -Htrue -mlatest -Upass -r Launcher.jar
"C:\Users\mrcat\.jdks\liberica-1.8.0_282\bin\jarsigner.exe" -keystore build/keeperjerry.jks -storepass PSP1448 -sigfile LAUNCHER Launcher.jar keeperjerry
"C:\Users\mrcat\.jdks\liberica-1.8.0_282\bin\pack200.exe" Launcher.pack.gz Launcher.jar

# Build LaunchServer.jar
echo Building LaunchServer.jar...
"C:\Users\mrcat\.jdks\liberica-1.8.0_282\bin\jar.exe" -uf LaunchServer.jar Launcher.pack.gz buildnumber
"C:\Users\mrcat\.jdks\liberica-1.8.0_282\bin\pack200.exe" -E9 -Htrue -mlatest -Upass -r LaunchServer.jar
"C:\Users\mrcat\.jdks\liberica-1.8.0_282\bin\jarsigner.exe" -keystore build/keeperjerry.jks -storepass PSP1448 -sigfile LAUNCHER LaunchServer.jar keeperjerry
rm Launcher.pack.gz